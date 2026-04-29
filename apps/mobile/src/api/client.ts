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

export type OperationStatus =
  | "PENDING"
  | "RUNNING"
  | "SUCCEEDED"
  | "FAILED_RETRYING"
  | "FAILED_PERMANENT"
  | "CANCELLED";

export type OperationAcceptedResponse = {
  operationId: string;
  status: OperationStatus;
  statusUrl: string;
};

export type OperationResponse = {
  id: string;
  type: "RESUME_PARSE";
  status: OperationStatus;
  resourceType?: string | null;
  resourceId?: string | null;
  errorCode?: string | null;
  errorMessage?: string | null;
  createdAt: string;
  updatedAt: string;
  completedAt?: string | null;
};

export type ResumeUploadFile = {
  uri: string;
  name: string;
  mimeType?: string | null;
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
  body?: unknown | FormData;
  accessToken?: string;
};

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = {
    Accept: "application/json",
  };

  const isFormData = options.body instanceof FormData;

  if (options.body !== undefined && !isFormData) {
    headers["Content-Type"] = "application/json";
  }

  if (options.accessToken) {
    headers.Authorization = `Bearer ${options.accessToken}`;
  }

  const requestBody: BodyInit | undefined =
    options.body === undefined
      ? undefined
      : isFormData
        ? (options.body as BodyInit)
        : JSON.stringify(options.body);

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: options.method ?? "GET",
    headers,
    body: requestBody,
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

export function uploadResume(
  accessToken: string,
  file: ResumeUploadFile,
): Promise<OperationAcceptedResponse> {
  const formData = new FormData();
  formData.append(
    "file",
    {
      uri: file.uri,
      name: file.name,
      type: file.mimeType ?? "application/octet-stream",
    } as unknown as Blob,
  );

  return request<OperationAcceptedResponse>("/resumes", {
    method: "POST",
    accessToken,
    body: formData,
  });
}

export function getOperation(accessToken: string, operationId: string): Promise<OperationResponse> {
  return request<OperationResponse>(`/operations/${operationId}`, {
    accessToken,
  });
}
