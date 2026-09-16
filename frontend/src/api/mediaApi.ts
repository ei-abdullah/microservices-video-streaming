import axios from "axios";
import {api} from "./client.ts";
import type {CreateUploadResponse, Media} from "../types/media.ts";

export const mediaApi = {
    getAllMedia: async (): Promise<Media[]> => {
        const response = await api.get<Media[]>("/api/v1/media");
        return response.data;
    },

    getMediaById: async (id: string): Promise<Media> => {
        const response = await api.get<Media>(`/api/v1/media/${id}`);
        return response.data;
    },

    createUpload: async (): Promise<CreateUploadResponse> => {
        const response = await api.post<CreateUploadResponse>(`/api/v1/media`);
        return response.data;
    },

    uploadToS3: async (
        presignedUrl: string,
        file: File,
        onProgress: (percent: number) => void
    ): Promise<void> => {
        await axios.put(presignedUrl, file, {
            headers: {
                'Content-Type': file.type || 'video/mp4',
            },
            onUploadProgress: (event) => {
                const total = event.total || file.size;
                const percent = Math.round((event.loaded * 100) / total);
                onProgress(percent)
            },
        });
    },

    completeUpload: async (mediaId: string): Promise<void> => {
        await api.post(`/api/v1/media/${mediaId}/complete`);
    },
};