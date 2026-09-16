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
  const [, setCreatedMediaId] = useState<string | null>(null);

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

      setUploadState('GETTING_URL');
      const uploadInfo = await mediaApi.createUpload();
      setCreatedMediaId(uploadInfo.mediaId);

      setUploadState('UPLOADING_S3');
      setProgress(0);
      await mediaApi.uploadToS3(uploadInfo.presignedUrl, file, (percent) => {
        setProgress(percent);
      });

      setUploadState('COMPLETING');
      await mediaApi.completeUpload(uploadInfo.mediaId);

      setUploadState('WAITING_TRANSCODE');
      pollTranscodingStatus(uploadInfo.mediaId);

    } catch (err: unknown) {
      setUploadState('ERROR');
      let msg = 'Upload failed. Check your connection.';
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
          setErrorMessage('Processing failed on the server.');
        }
      } catch {
        // Continue polling on transient error
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

  const statusLabel = () => {
    if (uploadState === 'GETTING_URL') return 'Preparing upload…';
    if (uploadState === 'UPLOADING_S3') return `Uploading — ${progress}%`;
    if (uploadState === 'COMPLETING') return 'Finishing up…';
    if (uploadState === 'WAITING_TRANSCODE') return 'Processing video…';
    return '';
  };

  const isActive = uploadState !== 'IDLE' && uploadState !== 'DONE' && uploadState !== 'ERROR';

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4">
      <div className="relative w-full max-w-md bg-zinc-950 border border-zinc-800 p-8">
        <button
          onClick={resetModal}
          className="absolute right-4 top-4 p-1 text-zinc-500 hover:text-white transition cursor-pointer"
        >
          <X className="h-4 w-4" />
        </button>

        <h2 className="text-base font-semibold text-white">Upload video</h2>
        <p className="mt-1 text-xs text-zinc-500">
          MP4, MOV or MKV. The video will be processed automatically.
        </p>

        {!isAuthenticated ? (
          <div className="mt-6 p-4 border border-zinc-800 text-center">
            <AlertCircle className="mx-auto h-6 w-6 text-zinc-500 mb-2" />
            <p className="text-sm text-zinc-400">Sign in to upload videos.</p>
          </div>
        ) : (
          <div className="mt-6 space-y-4">
            {/* Dropzone */}
            {uploadState === 'IDLE' && (
              <div
                onClick={() => fileInputRef.current?.click()}
                className="group flex flex-col items-center justify-center border border-dashed border-zinc-700 bg-black p-10 text-center transition hover:border-zinc-500 cursor-pointer"
              >
                <input
                  ref={fileInputRef}
                  type="file"
                  accept="video/mp4,video/mkv,video/quicktime,video/webm"
                  onChange={handleFileChange}
                  className="hidden"
                />
                <FileVideo className="h-8 w-8 text-zinc-600 group-hover:text-zinc-400 transition mb-3" />
                <span className="text-sm text-zinc-300">
                  {file ? file.name : 'Click to select a file'}
                </span>
                {file && (
                  <span className="text-xs text-zinc-500 mt-1">
                    {(file.size / (1024 * 1024)).toFixed(1)} MB
                  </span>
                )}
              </div>
            )}

            {/* Progress */}
            {isActive && (
              <div className="border border-zinc-800 bg-black p-5 space-y-4">
                <div className="flex items-center justify-between text-xs">
                  <span className="text-zinc-300 flex items-center gap-2">
                    <Loader2 className="h-3.5 w-3.5 animate-spin text-zinc-400" />
                    {statusLabel()}
                  </span>
                  {uploadState === 'UPLOADING_S3' && (
                    <span className="font-mono text-zinc-500">{progress}%</span>
                  )}
                </div>

                <div className="h-px w-full bg-zinc-800 overflow-hidden">
                  <div
                    className="h-full bg-white transition-all duration-300 ease-out"
                    style={{
                      width: uploadState === 'WAITING_TRANSCODE' ? '100%' : `${progress}%`,
                    }}
                  />
                </div>

                <p className="text-xs text-zinc-600 text-center">
                  {uploadState === 'WAITING_TRANSCODE'
                    ? 'This may take 10–30 seconds.'
                    : 'Do not close this window.'}
                </p>
              </div>
            )}

            {/* Done */}
            {uploadState === 'DONE' && (
              <div className="border border-zinc-800 p-6 text-center">
                <CheckCircle2 className="mx-auto h-8 w-8 text-white mb-3" />
                <h3 className="text-sm font-semibold text-white">Ready to watch</h3>
                <p className="text-xs text-zinc-500 mt-1">
                  Your video is ready.
                </p>
                <button
                  onClick={resetModal}
                  className="mt-5 bg-white px-5 py-2 text-xs font-semibold text-black hover:bg-zinc-200 transition cursor-pointer"
                >
                  Close
                </button>
              </div>
            )}

            {/* Error */}
            {errorMessage && (
              <div className="border border-zinc-800 p-4 flex items-start gap-2">
                <AlertCircle className="h-4 w-4 text-zinc-500 mt-0.5 shrink-0" />
                <p className="text-xs text-zinc-400">{errorMessage}</p>
              </div>
            )}

            {/* Actions */}
            {uploadState === 'IDLE' && (
              <div className="flex justify-end gap-3 pt-1">
                <button
                  onClick={resetModal}
                  className="px-4 py-2 text-xs font-medium text-zinc-500 hover:text-white transition cursor-pointer"
                >
                  Cancel
                </button>
                <button
                  disabled={!file}
                  onClick={handleUpload}
                  className="flex items-center gap-2 bg-white px-5 py-2 text-xs font-semibold text-black transition hover:bg-zinc-200 disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
                >
                  <UploadCloud className="h-3.5 w-3.5" />
                  <span>Upload</span>
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
