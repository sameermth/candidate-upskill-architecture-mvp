import { StatusBar } from 'expo-status-bar';
import { useCallback, useEffect, useState } from 'react';
import { Pressable, SafeAreaView, StyleSheet, Text, View } from 'react-native';

import { getHealth, type HealthResponse } from './src/api/client';

export default function App() {
  const [health, setHealth] = useState<HealthResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const loadHealth = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      setHealth(await getHealth());
    } catch (caughtError) {
      setHealth(null);
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to reach backend');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadHealth();
  }, [loadHealth]);

  return (
    <SafeAreaView style={styles.screen}>
      <StatusBar style="auto" />
      <View style={styles.header}>
        <Text style={styles.eyebrow}>Candidate Upskill</Text>
        <Text style={styles.title}>Interview readiness workspace</Text>
        <Text style={styles.subtitle}>
          The first mobile slice is wired to the backend health contract.
        </Text>
      </View>

      <View style={styles.panel}>
        <View style={styles.panelHeader}>
          <Text style={styles.panelTitle}>Backend status</Text>
          <View
            style={[
              styles.statusDot,
              health?.status === 'UP' ? styles.statusDotReady : styles.statusDotUnknown,
            ]}
          />
        </View>

        <Text style={styles.statusText}>
          {loading ? 'Checking...' : health ? health.status : 'Unavailable'}
        </Text>
        <Text style={styles.metaText}>
          {health ? `${health.service} at ${new Date(health.timestamp).toLocaleString()}` : error ?? 'Waiting for backend response'}
        </Text>

        <Pressable
          accessibilityRole="button"
          onPress={loadHealth}
          style={({ pressed }) => [styles.button, pressed && styles.buttonPressed]}
        >
          <Text style={styles.buttonText}>{loading ? 'Refreshing' : 'Refresh'}</Text>
        </Pressable>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: '#F6F4EF',
    padding: 24,
  },
  header: {
    gap: 10,
    paddingTop: 32,
    paddingBottom: 28,
  },
  eyebrow: {
    color: '#3F6B57',
    fontSize: 13,
    fontWeight: '700',
    letterSpacing: 0,
    textTransform: 'uppercase',
  },
  title: {
    color: '#181A1B',
    fontSize: 34,
    fontWeight: '800',
    letterSpacing: 0,
    lineHeight: 39,
  },
  subtitle: {
    color: '#4F5753',
    fontSize: 16,
    lineHeight: 23,
  },
  panel: {
    backgroundColor: '#FFFFFF',
    borderColor: '#DDD7CB',
    borderRadius: 8,
    borderWidth: 1,
    gap: 14,
    padding: 18,
  },
  panelHeader: {
    alignItems: 'center',
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  panelTitle: {
    color: '#222222',
    fontSize: 17,
    fontWeight: '700',
  },
  statusDot: {
    borderRadius: 6,
    height: 12,
    width: 12,
  },
  statusDotReady: {
    backgroundColor: '#2F8F5B',
  },
  statusDotUnknown: {
    backgroundColor: '#B9A681',
  },
  statusText: {
    color: '#111111',
    fontSize: 28,
    fontWeight: '800',
  },
  metaText: {
    color: '#626C67',
    fontSize: 14,
    lineHeight: 20,
  },
  button: {
    alignItems: 'center',
    alignSelf: 'flex-start',
    backgroundColor: '#1F4E3D',
    borderRadius: 6,
    paddingHorizontal: 18,
    paddingVertical: 12,
  },
  buttonPressed: {
    opacity: 0.82,
  },
  buttonText: {
    color: '#FFFFFF',
    fontSize: 15,
    fontWeight: '700',
  },
});
