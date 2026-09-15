export type MediaStatus = 'UPLOADING' | 'UPLOADED' | 'QUEUED' | 'READY' | 'FAILED';

export interface Media {
    id: string;
    title: string | null;
    status: MediaStatus;
    playbackUrl: string | null;
    duration: number | null;
    width: number | null;
    height: number | null;
    createdAt: string;
    updatedAt: string;
}

export interface CreateUploadResponse {
    id: string;
    preSignedUrl: string;
    status: string;
    createdAt: string;
    updatedAt: string;
}