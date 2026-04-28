import * as SecureStore from "expo-secure-store";

import type { AuthSessionResponse } from "../api/client";

const ACCESS_TOKEN_KEY = "candidate-upskill.access-token";
const REFRESH_TOKEN_KEY = "candidate-upskill.refresh-token";

export type StoredSessionTokens = {
  accessToken: string;
  refreshToken: string;
};

export async function saveSession(session: AuthSessionResponse): Promise<void> {
  await Promise.all([
    SecureStore.setItemAsync(ACCESS_TOKEN_KEY, session.accessToken),
    SecureStore.setItemAsync(REFRESH_TOKEN_KEY, session.refreshToken),
  ]);
}

export async function loadStoredSessionTokens(): Promise<StoredSessionTokens | null> {
  const [accessToken, refreshToken] = await Promise.all([
    SecureStore.getItemAsync(ACCESS_TOKEN_KEY),
    SecureStore.getItemAsync(REFRESH_TOKEN_KEY),
  ]);

  if (!accessToken || !refreshToken) {
    return null;
  }

  return { accessToken, refreshToken };
}

export async function clearStoredSession(): Promise<void> {
  await Promise.all([
    SecureStore.deleteItemAsync(ACCESS_TOKEN_KEY),
    SecureStore.deleteItemAsync(REFRESH_TOKEN_KEY),
  ]);
}
