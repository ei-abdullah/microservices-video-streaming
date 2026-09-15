import React, { useState, useRef } from 'react';
import { X, UploadCloud, FileVideo, CheckCircle2, AlertCircle, Loader2 } from 'lucide-react';
import { mediaApi } from '../api/mediaApi';
import { useAuth } from '../context/AuthContext';

interface UploadModalProps {
  isOpen: boolean;
  onClose: () => void;
  onUploadSuccess: () => void;
}

type UploadState = 'IDLE' | 'GETTING_URL' | 'UPLOADING_S3' | 'COMPLETING' | 'WAITING_TRANSCODE' | 'DONE' | 'ERROR';

export const UploadModal: React.FC<UploadModalProps> = ({ isOpen, onClose, onUploadSuccess }) => {
  const { isAuthenticated } = useAuth();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [file, setFile] = useState<File | null>(null);
  const [progress, setProgress] = useState<number>(0);
  const [uploadState, setUploadState] = useState<UploadState>('IDLE');
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [createdMediaId, setCreatedMediaId] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
      setErrorMessage(null);
    }
  };

  const handleUpload = async () => {
    if (!file) return;

    try {
      setErrorMessage(null);

      // Phase 1: Request S3 Presigned URL from media-service
      setUploadState('GETTING_URL');
      const uploadInfo = await mediaApi.createUpload();
      setCreatedMediaId(uploadInfo.id);

      // Phase 2: Direct Binary PUT to S3 with progress tracking
      setUploadState('UPLOADING_S3');
      setProgress(0);
      await mediaApi.uploadToS3(uploadInfo.preSignedUrl, file, (percent) => {
        setProgress(percent);
      });

      // Phase 3: Notify backend to enqueue transcoding in RabbitMQ
      setUploadState('COMPLETING');
      await mediaApi.completeUpload(uploadInfo.id);

      // Phase 4: Wait for transcoding status by polling
      setUploadState('WAITING_TRANSCODE');
      pollTranscodingStatus(uploadInfo.id);

    } catch (err: unknown) {
      setUploadState('ERROR');
      let msg = 'Upload failed. Check connection.';
      if (err instanceof Error) {
        msg = err.message;
      }
      setErrorMessage(msg);
    }
  };

  const pollTranscodingStatus = (mediaId: string) => {
    const interval = setInterval(async () => {
      try {
        const media = await mediaApi.getMediaById(mediaId);
        if (media.status === 'READY') {
          clearInterval(interval);
          setUploadState('DONE');
          onUploadSuccess();
        } else if (media.status === 'FAILED') {
          clearInterval(interval);
          setUploadState('ERROR');
          setErrorMessage('FFmpeg transcoding failed on the server.');
        }
      } catch {
        // Continue polling if transient network error
      }
    }, 2500);
  };

  const resetModal = () => {
    setFile(null);
    setProgress(0);
    setUploadState('IDLE');
    setErrorMessage(null);
    setCreatedMediaId(null);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 p-4 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="relative w-full max-w-lg overflow-hidden rounded-2xl border border-slate-800 bg-slate-900 p-6 shadow-2xl">
        {/* Close Button */}
        <button
          onClick={resetModal}
          className="absolute right-4 top-4 rounded-lg p-1.5 text-slate-400 hover:bg-slate-800 hover:text-white transition cursor-pointer"
        >
          <X className="h-5 w-5" />
        </button>

        <h2 className="text-xl font-bold text-white flex items-center gap-2">
          <UploadCloud className="h-6 w-6 text-indigo-400" />
          Upload New Video
        </h2>
        <p className="mt-1 text-sm text-slate-400">
          Upload video directly to AWS S3. It will be transcoded to adaptive HLS automatically.
        </p>

        {!isAuthenticated ? (
          <div className="mt-6 rounded-xl border border-amber-500/20 bg-amber-500/10 p-4 text-center">
            <AlertCircle className="mx-auto h-8 w-8 text-amber-400 mb-2" />
            <p className="text-sm font-medium text-amber-200">Authentication Required</p>
            <p className="text-xs text-amber-300/80 mt-1">
              Please sign in with a verified account before uploading videos.
            </p>
          </div>
        ) : (
          <div className="mt-6 space-y-4">
            {/* File Dropzone */}
            {uploadState === 'IDLE' && (
              <div
                onClick={() => fileInputRef.current?.click()}
                className="group flex flex-col items-center justify-center rounded-xl border-2 border-dashed border-slate-700 bg-slate-950/50 p-8 text-center transition hover:border-indigo-500 hover:bg-indigo-950/10 cursor-pointer"
              >
                <input
                  ref={fileInputRef}
                  type="file"
                  accept="video/mp4,video/mkv,video/quicktime,video/webm"
                  onChange={handleFileChange}
                  className="hidden"
                />
                <FileVideo className="h-12 w-12 text-slate-500 group-hover:text-indigo-400 transition mb-3" />
                <span className="text-sm font-semibold text-slate-200">
                  {file ? file.name : 'Click or drag video file here'}
                </span>
                <span className="text-xs text-slate-500 mt-1">
                  MP4, MOV, MKV up to 4K resolution
                </span>
                {file && (
                  <span className="mt-2 text-xs font-mono text-indigo-400">
                    {(file.size / (1024 * 1024)).toFixed(1)} MB
                  </span>
                )}
              </div>
            )}

            {/* Progress States */}
            {uploadState !== 'IDLE' && uploadState !== 'DONE' && uploadState !== 'ERROR' && (
              <div className="rounded-xl border border-slate-800 bg-slate-950 p-5 space-y-4">
                <div className="flex items-center justify-between text-sm">
                  <span className="font-medium text-slate-200 flex items-center gap-2">
                    <Loader2 className="h-4 w-4 animate-spin text-indigo-400" />
                    {uploadState === 'GETTING_URL' && 'Requesting S3 Presigned URL...'}
                    {uploadState === 'UPLOADING_S3' && `Uploading to AWS S3: ${progress}%`}
                    {uploadState === 'COMPLETING' && 'Notifying transcoding worker...'}
                    {uploadState === 'WAITING_TRANSCODE' && 'FFmpeg transcoding in progress...'}
                  </span>
                  <span className="text-xs font-mono text-slate-400">
                    {uploadState === 'UPLOADING_S3' ? `${progress}%` : 'Processing'}
                  </span>
                </div>

                {/* Progress bar */}
                <div className="h-2 w-full overflow-hidden rounded-full bg-slate-800">
                  <div
                    className="h-full bg-gradient-to-r from-indigo-500 to-violet-500 transition-all duration-300 ease-out"
                    style={{
                      width: uploadState === 'WAITING_TRANSCODE' ? '100%' : `${progress}%`,
                    }}
                  />
                </div>

                <p className="text-xs text-slate-500 text-center">
                  {uploadState === 'WAITING_TRANSCODE'
                    ? 'Worker is generating multi-quality HLS segments (.m3u8). This may take 10-30 seconds.'
                    : 'Do not close this modal while uploading directly to storage.'}
                </p>
              </div>
            )}

            {/* Done state */}
            {uploadState === 'DONE' && (
              <div className="rounded-xl border border-emerald-500/20 bg-emerald-500/10 p-5 text-center">
                <CheckCircle2 className="mx-auto h-10 w-10 text-emerald-400 mb-2" />
                <h3 className="text-base font-bold text-emerald-200">Transcoding Complete!</h3>
                <p className="text-xs text-emerald-300/80 mt-1">
                  Your video has been transcoded into HLS and is now ready for streaming.
                </p>
                {createdMediaId && (
                  <p className="text-[11px] font-mono text-emerald-400/80 mt-2">
                    Media ID: {createdMediaId}
                  </p>
                )}
                <button
                  onClick={resetModal}
                  className="mt-4 rounded-lg bg-emerald-600 px-4 py-2 text-xs font-semibold text-white hover:bg-emerald-500 transition cursor-pointer"
                >
                  View in Video Feed
                </button>
              </div>
            )}

            {/* Error state */}
            {errorMessage && (
              <div className="rounded-xl border border-rose-500/20 bg-rose-500/10 p-4 text-center">
                <AlertCircle className="mx-auto h-7 w-7 text-rose-400 mb-1" />
                <p className="text-sm font-semibold text-rose-300">{errorMessage}</p>
              </div>
            )}

            {/* Actions */}
            {uploadState === 'IDLE' && (
              <div className="flex justify-end gap-3 pt-2">
                <button
                  onClick={resetModal}
                  className="rounded-lg border border-slate-800 bg-slate-900 px-4 py-2 text-sm font-medium text-slate-300 hover:bg-slate-800 transition cursor-pointer"
                >
                  Cancel
                </button>
                <button
                  disabled={!file}
                  onClick={handleUpload}
                  className="flex items-center gap-2 rounded-lg bg-indigo-600 px-5 py-2 text-sm font-medium text-white transition hover:bg-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer"
                >
                  <UploadCloud className="h-4 w-4" />
                  <span>Start Upload</span>
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
