import React from 'react';
import { Play, Clock, AlertTriangle, Loader2 } from 'lucide-react';
import type { Media } from '../types/media';

interface VideoCardProps {
  media: Media;
  isSelected?: boolean;
  onSelect: (media: Media) => void;
}

export const VideoCard: React.FC<VideoCardProps> = ({ media, isSelected, onSelect }) => {
  const formatDuration = (seconds: number | null) => {
    if (!seconds) return '0:00';
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
  };

  const formatDate = (isoString: string | Date) => {
    try {
      return new Date(isoString).toLocaleDateString(undefined, {
        month: 'short',
        day: 'numeric',
      });
    } catch {
      return '';
    }
  };

  const isReady = media.status === 'READY';
  const isFailed = media.status === 'FAILED';
  const isProcessing = !isReady && !isFailed;

  return (
    <div
      onClick={() => isReady && onSelect(media)}
      className={`group relative flex flex-col overflow-hidden border bg-zinc-950 transition duration-150 ${
        isSelected
          ? 'border-white'
          : 'border-zinc-800 hover:border-zinc-600'
      } ${isReady ? 'cursor-pointer' : 'cursor-default opacity-60'}`}
    >
      {/* Thumbnail */}
      <div className="relative aspect-video w-full overflow-hidden bg-black flex items-center justify-center">
        {isReady && (
          <div className="relative z-10 flex h-10 w-10 items-center justify-center rounded-full bg-white text-black transition duration-150 group-hover:scale-110">
            <Play className="h-4 w-4 fill-current ml-0.5" />
          </div>
        )}

        {isProcessing && (
          <div className="relative z-10 flex flex-col items-center gap-1.5 text-zinc-400">
            <Loader2 className="h-6 w-6 animate-spin" />
            <span className="text-xs">Processing…</span>
          </div>
        )}

        {isFailed && (
          <div className="relative z-10 flex flex-col items-center gap-1 text-zinc-500">
            <AlertTriangle className="h-6 w-6" />
            <span className="text-xs">Failed</span>
          </div>
        )}

        {/* Duration */}
        {isReady && media.duration && (
          <span className="absolute bottom-2 right-2 z-10 bg-black/80 px-1.5 py-0.5 text-xs font-mono text-white">
            {formatDuration(media.duration)}
          </span>
        )}
      </div>

      {/* Info */}
      <div className="flex flex-1 flex-col p-3">
        <h3 className="line-clamp-1 text-sm font-medium text-white">
          {media.title || `Untitled`}
        </h3>

        <div className="mt-2 flex items-center justify-between text-xs text-zinc-500">
          <div className="flex items-center gap-1">
            <Clock className="h-3 w-3" />
            <span>{formatDate(media.createdAt)}</span>
          </div>

          {isReady && (
            <span className="text-zinc-500">Ready</span>
          )}
          {isProcessing && (
            <span className="text-zinc-500">Processing</span>
          )}
          {isFailed && (
            <span className="text-zinc-500">Failed</span>
          )}
        </div>
      </div>
    </div>
  );
};
