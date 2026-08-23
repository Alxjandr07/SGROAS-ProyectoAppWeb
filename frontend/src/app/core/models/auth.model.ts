export interface LoginRequest {
  email: string;
  password: string;
}

export interface MensajeResponse {
  mensaje: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  nombre: string;
  email: string;
  rol: string;
}