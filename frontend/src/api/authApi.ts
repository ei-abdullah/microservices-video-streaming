import {api} from "./client.ts";
import type {LoginResponse, SignupResponse} from "../types/auth.ts";

export const authApi = {
    login: async (email: string, password: string): Promise<LoginResponse> => {
        const response = await api.post<LoginResponse>("/api/v1/auth/login", {
            email,
            password,
        });
        return response.data;
    },

    signup: async (email: string, password: string): Promise<SignupResponse> => {
        const response = await api.post<SignupResponse>(`/api/v1/auth/signup`, {
            email,
            password,
        });
        return response.data;
    }
};