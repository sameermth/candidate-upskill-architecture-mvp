import { StatusBar } from "expo-status-bar";
import * as DocumentPicker from "expo-document-picker";
import { useCallback, useEffect, useMemo, useState } from "react";
import {
  KeyboardAvoidingView,
  Platform,
  Pressable,
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";

import {
  getCurrentUser,
  getOperation,
  login,
  refreshSession,
  signup,
  uploadResume,
  type AuthSessionResponse,
  type OperationResponse,
  type UserProfile,
} from "./src/api/client";
import {
  clearStoredSession,
  loadStoredSessionTokens,
  saveSession,
} from "./src/auth/sessionStorage";

type AuthMode = "login" | "signup";

type ActiveSession = {
  accessToken: string;
  refreshToken: string;
  user: UserProfile;
};

export default function App() {
  const [mode, setMode] = useState<AuthMode>("login");
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [session, setSession] = useState<ActiveSession | null>(null);
  const [loading, setLoading] = useState(false);
  const [resumeLoading, setResumeLoading] = useState(false);
  const [resumeOperation, setResumeOperation] = useState<OperationResponse | null>(null);
  const [bootstrapping, setBootstrapping] = useState(true);
  const [message, setMessage] = useState<string | null>(null);

  const title = mode === "login" ? "Welcome back" : "Create your account";
  const actionLabel = mode === "login" ? "Sign in" : "Create account";
  const canSubmit = useMemo(() => {
    const baseFieldsReady = email.trim().length > 0 && password.length >= 8;
    return mode === "login" ? baseFieldsReady : baseFieldsReady && name.trim().length > 0;
  }, [email, mode, name, password]);

  const applySession = useCallback(async (nextSession: AuthSessionResponse) => {
    await saveSession(nextSession);
    setSession(nextSession);
  }, []);

  useEffect(() => {
    async function restoreSession() {
      try {
        const storedTokens = await loadStoredSessionTokens();
        if (!storedTokens) {
          return;
        }

        try {
          const user = await getCurrentUser(storedTokens.accessToken);
          setSession({ ...storedTokens, user });
        } catch {
          const refreshed = await refreshSession(storedTokens.refreshToken);
          await applySession(refreshed);
        }
      } catch {
        await clearStoredSession();
      } finally {
        setBootstrapping(false);
      }
    }

    void restoreSession();
  }, [applySession]);

  const submit = useCallback(async () => {
    if (!canSubmit) {
      setMessage("Use a valid email and a password with at least 8 characters.");
      return;
    }

    setLoading(true);
    setMessage(null);

    try {
      const nextSession =
        mode === "login"
          ? await login({ email: email.trim(), password })
          : await signup({ name: name.trim(), email: email.trim(), password });

      await applySession(nextSession);
      setPassword("");
    } catch (caughtError) {
      setMessage(caughtError instanceof Error ? caughtError.message : "Authentication failed");
    } finally {
      setLoading(false);
    }
  }, [applySession, canSubmit, email, mode, name, password]);

  const signOut = useCallback(async () => {
    await clearStoredSession();
    setSession(null);
    setResumeOperation(null);
    setMessage(null);
  }, []);

  const refresh = useCallback(async () => {
    if (!session) {
      return;
    }

    setLoading(true);
    setMessage(null);

    try {
      await applySession(await refreshSession(session.refreshToken));
      setMessage("Session refreshed.");
    } catch (caughtError) {
      await clearStoredSession();
      setSession(null);
      setMessage(caughtError instanceof Error ? caughtError.message : "Session expired");
    } finally {
      setLoading(false);
    }
  }, [applySession, session]);

  const refreshOperation = useCallback(async () => {
    if (!session || !resumeOperation) {
      return;
    }

    setResumeLoading(true);
    setMessage(null);

    try {
      setResumeOperation(await getOperation(session.accessToken, resumeOperation.id));
    } catch (caughtError) {
      setMessage(caughtError instanceof Error ? caughtError.message : "Unable to refresh resume status");
    } finally {
      setResumeLoading(false);
    }
  }, [resumeOperation, session]);

  const uploadSelectedResume = useCallback(async () => {
    if (!session) {
      return;
    }

    setResumeLoading(true);
    setMessage(null);

    try {
      const result = await DocumentPicker.getDocumentAsync({
        copyToCacheDirectory: true,
        multiple: false,
        type: [
          "application/pdf",
          "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
          "text/plain",
        ],
      });

      if (result.canceled) {
        return;
      }

      const asset = result.assets[0];
      const accepted = await uploadResume(session.accessToken, {
        uri: asset.uri,
        name: asset.name,
        mimeType: asset.mimeType,
      });

      setResumeOperation(await getOperation(session.accessToken, accepted.operationId));
      setMessage("Resume uploaded. Parsing is queued.");
    } catch (caughtError) {
      setMessage(caughtError instanceof Error ? caughtError.message : "Resume upload failed");
    } finally {
      setResumeLoading(false);
    }
  }, [session]);

  if (bootstrapping) {
    return (
      <SafeAreaView style={styles.screen}>
        <StatusBar style="auto" />
        <View style={styles.centered}>
          <Text style={styles.eyebrow}>Candidate Upskill</Text>
          <Text style={styles.statusTitle}>Restoring session</Text>
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.screen}>
      <StatusBar style="auto" />
      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : undefined}
        style={styles.flex}
      >
        <ScrollView contentContainerStyle={styles.content} keyboardShouldPersistTaps="handled">
          <View style={styles.header}>
            <Text style={styles.eyebrow}>Candidate Upskill</Text>
            <Text style={styles.title}>
              {session ? "Readiness starts here" : title}
            </Text>
            <Text style={styles.subtitle}>
              {session
                ? "You are signed in and ready for the first preparation step."
                : "Sign in to continue building the interview prep workspace."}
            </Text>
          </View>

          {session ? (
            <>
              <View style={styles.panel}>
                <Text style={styles.panelLabel}>Signed in as</Text>
                <Text style={styles.userName}>{session.user.name}</Text>
                <Text style={styles.userEmail}>{session.user.email}</Text>

                <View style={styles.buttonRow}>
                  <Pressable
                    accessibilityRole="button"
                    onPress={refresh}
                    style={({ pressed }) => [styles.secondaryButton, pressed && styles.buttonPressed]}
                  >
                    <Text style={styles.secondaryButtonText}>
                      {loading ? "Refreshing" : "Refresh session"}
                    </Text>
                  </Pressable>

                  <Pressable
                    accessibilityRole="button"
                    onPress={signOut}
                    style={({ pressed }) => [styles.dangerButton, pressed && styles.buttonPressed]}
                  >
                    <Text style={styles.dangerButtonText}>Sign out</Text>
                  </Pressable>
                </View>
              </View>

              <View style={styles.panel}>
                <Text style={styles.panelLabel}>Resume</Text>
                <Text style={styles.panelTitle}>Upload your latest resume</Text>
                <Text style={styles.panelCopy}>
                  PDF, DOCX, and plain text files are accepted. You can track the upload status after it starts.
                </Text>

                {resumeOperation && (
                  <View style={styles.operationBox}>
                    <Text style={styles.operationLabel}>Operation</Text>
                    <Text style={styles.operationValue}>{resumeOperation.status}</Text>
                    <Text style={styles.operationMeta}>{resumeOperation.type}</Text>
                  </View>
                )}

                <View style={styles.buttonRow}>
                  <Pressable
                    accessibilityRole="button"
                    disabled={resumeLoading}
                    onPress={uploadSelectedResume}
                    style={({ pressed }) => [
                      styles.primaryButton,
                      resumeLoading && styles.buttonDisabled,
                      pressed && styles.buttonPressed,
                    ]}
                  >
                    <Text style={styles.primaryButtonText}>
                      {resumeLoading ? "Uploading..." : "Choose resume"}
                    </Text>
                  </Pressable>

                  {resumeOperation && (
                    <Pressable
                      accessibilityRole="button"
                      disabled={resumeLoading}
                      onPress={refreshOperation}
                      style={({ pressed }) => [styles.secondaryButton, pressed && styles.buttonPressed]}
                    >
                      <Text style={styles.secondaryButtonText}>Refresh status</Text>
                    </Pressable>
                  )}
                </View>
              </View>
            </>
          ) : (
            <View style={styles.panel}>
              <View style={styles.segmented}>
                <Pressable
                  accessibilityRole="button"
                  onPress={() => setMode("login")}
                  style={[styles.segment, mode === "login" && styles.segmentActive]}
                >
                  <Text style={[styles.segmentText, mode === "login" && styles.segmentTextActive]}>
                    Sign in
                  </Text>
                </Pressable>
                <Pressable
                  accessibilityRole="button"
                  onPress={() => setMode("signup")}
                  style={[styles.segment, mode === "signup" && styles.segmentActive]}
                >
                  <Text style={[styles.segmentText, mode === "signup" && styles.segmentTextActive]}>
                    Create
                  </Text>
                </Pressable>
              </View>

              {mode === "signup" && (
                <View style={styles.fieldGroup}>
                  <Text style={styles.label}>Name</Text>
                  <TextInput
                    autoCapitalize="words"
                    onChangeText={setName}
                    placeholder="Sameer Khan"
                    placeholderTextColor="#8C8F88"
                    style={styles.input}
                    value={name}
                  />
                </View>
              )}

              <View style={styles.fieldGroup}>
                <Text style={styles.label}>Email</Text>
                <TextInput
                  autoCapitalize="none"
                  keyboardType="email-address"
                  onChangeText={setEmail}
                  placeholder="you@example.com"
                  placeholderTextColor="#8C8F88"
                  style={styles.input}
                  value={email}
                />
              </View>

              <View style={styles.fieldGroup}>
                <Text style={styles.label}>Password</Text>
                <TextInput
                  onChangeText={setPassword}
                  placeholder="Minimum 8 characters"
                  placeholderTextColor="#8C8F88"
                  secureTextEntry
                  style={styles.input}
                  value={password}
                />
              </View>

              <Pressable
                accessibilityRole="button"
                disabled={loading || !canSubmit}
                onPress={submit}
                style={({ pressed }) => [
                  styles.primaryButton,
                  (!canSubmit || loading) && styles.buttonDisabled,
                  pressed && styles.buttonPressed,
                ]}
              >
                <Text style={styles.primaryButtonText}>
                  {loading ? "Working..." : actionLabel}
                </Text>
              </Pressable>
            </View>
          )}

          {message && <Text style={styles.message}>{message}</Text>}
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  flex: {
    flex: 1,
  },
  screen: {
    flex: 1,
    backgroundColor: "#F6F4EF",
  },
  content: {
    gap: 22,
    padding: 24,
    paddingTop: 44,
  },
  centered: {
    flex: 1,
    justifyContent: "center",
    padding: 24,
  },
  header: {
    gap: 10,
  },
  eyebrow: {
    color: "#3F6B57",
    fontSize: 13,
    fontWeight: "700",
    letterSpacing: 0,
    textTransform: "uppercase",
  },
  title: {
    color: "#181A1B",
    fontSize: 34,
    fontWeight: "800",
    letterSpacing: 0,
    lineHeight: 39,
  },
  subtitle: {
    color: "#4F5753",
    fontSize: 16,
    lineHeight: 23,
  },
  statusTitle: {
    color: "#181A1B",
    fontSize: 24,
    fontWeight: "800",
    marginTop: 8,
  },
  panel: {
    backgroundColor: "#FFFFFF",
    borderColor: "#DDD7CB",
    borderRadius: 8,
    borderWidth: 1,
    gap: 16,
    padding: 18,
  },
  panelLabel: {
    color: "#626C67",
    fontSize: 14,
    fontWeight: "700",
    textTransform: "uppercase",
  },
  panelTitle: {
    color: "#111111",
    fontSize: 21,
    fontWeight: "800",
  },
  panelCopy: {
    color: "#59615D",
    fontSize: 14,
    lineHeight: 20,
  },
  userName: {
    color: "#111111",
    fontSize: 26,
    fontWeight: "800",
  },
  userEmail: {
    color: "#59615D",
    fontSize: 15,
  },
  operationBox: {
    backgroundColor: "#F9F7F1",
    borderColor: "#DDD7CB",
    borderRadius: 8,
    borderWidth: 1,
    gap: 5,
    padding: 14,
  },
  operationLabel: {
    color: "#626C67",
    fontSize: 12,
    fontWeight: "800",
    textTransform: "uppercase",
  },
  operationValue: {
    color: "#1F4E3D",
    fontSize: 22,
    fontWeight: "900",
  },
  operationMeta: {
    color: "#59615D",
    fontSize: 13,
  },
  segmented: {
    backgroundColor: "#EEE8DC",
    borderRadius: 8,
    flexDirection: "row",
    padding: 4,
  },
  segment: {
    alignItems: "center",
    borderRadius: 6,
    flex: 1,
    paddingVertical: 10,
  },
  segmentActive: {
    backgroundColor: "#FFFFFF",
  },
  segmentText: {
    color: "#59615D",
    fontSize: 14,
    fontWeight: "700",
  },
  segmentTextActive: {
    color: "#1F4E3D",
  },
  fieldGroup: {
    gap: 7,
  },
  label: {
    color: "#2C332F",
    fontSize: 14,
    fontWeight: "700",
  },
  input: {
    backgroundColor: "#F9F7F1",
    borderColor: "#D7D1C7",
    borderRadius: 6,
    borderWidth: 1,
    color: "#151515",
    fontSize: 16,
    minHeight: 48,
    paddingHorizontal: 13,
  },
  buttonRow: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 10,
  },
  primaryButton: {
    alignItems: "center",
    backgroundColor: "#1F4E3D",
    borderRadius: 6,
    paddingHorizontal: 18,
    paddingVertical: 14,
  },
  secondaryButton: {
    alignItems: "center",
    backgroundColor: "#E8F0EA",
    borderRadius: 6,
    paddingHorizontal: 16,
    paddingVertical: 12,
  },
  dangerButton: {
    alignItems: "center",
    backgroundColor: "#F5E5DE",
    borderRadius: 6,
    paddingHorizontal: 16,
    paddingVertical: 12,
  },
  buttonDisabled: {
    opacity: 0.55,
  },
  buttonPressed: {
    opacity: 0.82,
  },
  primaryButtonText: {
    color: "#FFFFFF",
    fontSize: 15,
    fontWeight: "800",
  },
  secondaryButtonText: {
    color: "#1F4E3D",
    fontSize: 14,
    fontWeight: "800",
  },
  dangerButtonText: {
    color: "#9A3D22",
    fontSize: 14,
    fontWeight: "800",
  },
  message: {
    color: "#7A351F",
    fontSize: 14,
    lineHeight: 20,
  },
});
