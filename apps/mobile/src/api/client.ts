import { API_BASE_URL } from "../config/env";

export type HealthResponse = {
  status: string;
  service: string;
  timestamp: string;
};

export type UserProfile = {
  id: string;
  email: string;
  name: string;
};

export type AuthSessionResponse = {
  accessToken: string;
  refreshToken: string;
  user: UserProfile;
};

export type SignupRequest = {
  name: string;
  email: string;
  password: string;
};

export type LoginRequest = {
  email: string;
  password: string;
};

type RequestOptions = {
  method?: "GET" | "POST";
  body?: unknown;
  accessToken?: string;
};

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = {
    Accept: "application/json",
  };

  if (options.body !== undefined) {
    headers["Content-Type"] = "application/json";
  }

  if (options.accessToken) {
    headers.Authorization = `Bearer ${options.accessToken}`;
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: options.method ?? "GET",
    headers,
    body: options.body === undefined ? undefined : JSON.stringify(options.body),
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed with status ${response.status}`);
  }

  return response.json() as Promise<T>;
}

export function getHealth(): Promise<HealthResponse> {
  return request<HealthResponse>("/health");
}

export function signup(payload: SignupRequest): Promise<AuthSessionResponse> {
  return request<AuthSessionResponse>("/auth/signup", {
    method: "POST",
    body: payload,
  });
}

export function login(payload: LoginRequest): Promise<AuthSessionResponse> {
  return request<AuthSessionResponse>("/auth/login", {
    method: "POST",
    body: payload,
  });
}

export function refreshSession(refreshToken: string): Promise<AuthSessionResponse> {
  return request<AuthSessionResponse>("/auth/refresh", {
    method: "POST",
    body: { refreshToken },
  });
}

export function getCurrentUser(accessToken: string): Promise<UserProfile> {
  return request<UserProfile>("/auth/me", {
    accessToken,
  });
}
