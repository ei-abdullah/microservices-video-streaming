import React, { useEffect, useRef, useState } from 'react';
import Hls from 'hls.js';
import { AlertCircle, Loader2 } from 'lucide-react';

interface VideoPlayerProps {
  src: string;
  title?: string;
  autoPlay?: boolean;
}

export const VideoPlayer: React.FC<VideoPlayerProps> = ({ src, title, autoPlay = true }) => {
  const videoRef = useRef<HTMLVideoElement>(null);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const video = videoRef.current;
    if (!video || !src) return;

    setError(null);
    setIsLoading(true);

    let hls: Hls | null = null;

    if (Hls.isSupported()) {
      hls = new Hls({
        enableWorker: true,
        lowLatencyMode: true,
        backBufferLength: 90,
      });

      hls.loadSource(src);
      hls.attachMedia(video);

      hls.on(Hls.Events.MANIFEST_PARSED, () => {
        setIsLoading(false);
        if (autoPlay) {
          video.play().catch(() => {
            // Autoplay with audio was blocked by browser, user can click play
          });
        }
      });

      hls.on(Hls.Events.ERROR, (_, data) => {
        if (data.fatal) {
          switch (data.type) {
            case Hls.ErrorTypes.NETWORK_ERROR:
              hls?.startLoad();
              break;
            case Hls.ErrorTypes.MEDIA_ERROR:
              hls?.recoverMediaError();
              break;
            default:
              hls?.destroy();
              setError('Failed to load video stream. Check S3 URL and CORS permissions.');
              setIsLoading(false);
              break;
          }
        }
      });
    } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
      // Native Safari HLS
      video.src = src;
      video.addEventListener('loadedmetadata', () => {
        setIsLoading(false);
        if (autoPlay) {
          video.play().catch(() => {});
        }
      });
      video.addEventListener('error', () => {
        setError('Error streaming HLS video.');
        setIsLoading(false);
      });
    } else {
      setError('HLS playback is not supported in this browser.');
      setIsLoading(false);
    }

    return () => {
      if (hls) {
        hls.destroy();
      }
    };
  }, [src, autoPlay]);

  return (
    <div className="relative w-full overflow-hidden rounded-2xl bg-black shadow-2xl border border-slate-800">
      {/* 16:9 Aspect Ratio Container */}
      <div className="relative aspect-video w-full">
        <video
          ref={videoRef}
          controls
          playsInline
          className="h-full w-full object-contain"
        />

        {/* Loading Overlay */}
        {isLoading && !error && (
          <div className="absolute inset-0 flex flex-col items-center justify-center bg-black/60 backdrop-blur-xs text-white">
            <Loader2 className="h-10 w-10 animate-spin text-indigo-500" />
            <span className="mt-2 text-sm text-slate-300">Buffering HLS Stream...</span>
          </div>
        )}

        {/* Error Overlay */}
        {error && (
          <div className="absolute inset-0 flex flex-col items-center justify-center bg-slate-950/90 p-6 text-center text-white">
            <AlertCircle className="h-12 w-12 text-rose-500 mb-2" />
            <p className="text-base font-semibold text-rose-300">{error}</p>
            <p className="text-xs text-slate-400 mt-2 max-w-md break-all">{src}</p>
          </div>
        )}
      </div>

      {title && (
        <div className="bg-slate-900/90 px-4 py-2.5 border-t border-slate-800 flex items-center justify-between">
          <span className="font-medium text-slate-200 text-sm">{title}</span>
          <span className="text-xs text-indigo-400 font-mono bg-indigo-500/10 px-2 py-0.5 rounded border border-indigo-500/20">
            HLS VOD
          </span>
        </div>
      )}
    </div>
  );
};
