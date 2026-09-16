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
              setError('Could not load video. Please try again.');
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
        setError('Error loading video.');
        setIsLoading(false);
      });
    } else {
      setError('Video playback is not supported in this browser.');
      setIsLoading(false);
    }

    return () => {
      if (hls) {
        hls.destroy();
      }
    };
  }, [src, autoPlay]);

  return (
    <div className="relative w-full overflow-hidden bg-black border border-zinc-800">
      <div className="relative aspect-video w-full">
        <video
          ref={videoRef}
          controls
          playsInline
          className="h-full w-full object-contain"
        />

        {/* Loading */}
        {isLoading && !error && (
          <div className="absolute inset-0 flex flex-col items-center justify-center bg-black/80 text-white">
            <Loader2 className="h-8 w-8 animate-spin text-white" />
          </div>
        )}

        {/* Error */}
        {error && (
          <div className="absolute inset-0 flex flex-col items-center justify-center bg-black p-6 text-center">
            <AlertCircle className="h-8 w-8 text-zinc-500 mb-2" />
            <p className="text-sm text-zinc-400">{error}</p>
          </div>
        )}
      </div>

      {title && (
        <div className="px-4 py-2.5 border-t border-zinc-800">
          <span className="text-sm text-zinc-300">{title}</span>
        </div>
      )}
    </div>
  );
};
