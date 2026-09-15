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
      className={`group relative flex flex-col overflow-hidden rounded-xl border bg-slate-900/60 transition duration-200 ${
        isSelected
          ? 'border-indigo-500 shadow-lg shadow-indigo-500/10 ring-2 ring-indigo-500/40'
          : 'border-slate-800 hover:border-slate-700 hover:bg-slate-900'
      } ${isReady ? 'cursor-pointer hover:-translate-y-1' : 'cursor-default opacity-85'}`}
    >
      {/* Thumbnail Aspect Ratio Area */}
      <div className="relative aspect-video w-full overflow-hidden bg-slate-950 flex items-center justify-center">
        {/* Ambient Gradient Background */}
        <div className="absolute inset-0 bg-gradient-to-br from-indigo-950/40 via-slate-950 to-slate-900" />

        {isReady && (
          <div className="relative z-10 flex h-12 w-12 items-center justify-center rounded-full bg-indigo-600/90 text-white shadow-lg transition duration-200 group-hover:scale-110 group-hover:bg-indigo-500">
            <Play className="h-5 w-5 fill-current ml-0.5" />
          </div>
        )}

        {isProcessing && (
          <div className="relative z-10 flex flex-col items-center gap-1.5 text-amber-400">
            <Loader2 className="h-8 w-8 animate-spin" />
            <span className="text-xs font-medium">Processing HLS...</span>
          </div>
        )}

        {isFailed && (
          <div className="relative z-10 flex flex-col items-center gap-1 text-rose-400">
            <AlertTriangle className="h-7 w-7" />
            <span className="text-xs font-semibold">Transcoding Failed</span>
          </div>
        )}

        {/* Duration Badge */}
        {isReady && media.duration && (
          <span className="absolute bottom-2 right-2 z-10 rounded bg-black/80 px-1.5 py-0.5 text-xs font-mono font-medium text-white backdrop-blur-xs">
            {formatDuration(media.duration)}
          </span>
        )}

        {/* Resolution Badge */}
        {isReady && media.height && (
          <span className="absolute top-2 left-2 z-10 rounded bg-slate-900/90 px-1.5 py-0.5 text-[10px] font-bold text-slate-300 border border-slate-700">
            {media.height}p
          </span>
        )}
      </div>

      {/* Meta Info */}
      <div className="flex flex-1 flex-col p-3.5">
        <h3 className="line-clamp-1 text-sm font-semibold text-slate-100 group-hover:text-indigo-400">
          {media.title || `Video #${media.id.slice(0, 8)}`}
        </h3>

        <div className="mt-2.5 flex items-center justify-between text-xs text-slate-400">
          <div className="flex items-center gap-1">
            <Clock className="h-3.5 w-3.5" />
            <span>{formatDate(media.createdAt)}</span>
          </div>

          {/* Status Chip */}
          {isReady && (
            <span className="inline-flex items-center rounded-full bg-emerald-500/10 px-2 py-0.5 text-[11px] font-medium text-emerald-400 border border-emerald-500/20">
              Ready
            </span>
          )}
          {isProcessing && (
            <span className="inline-flex items-center rounded-full bg-amber-500/10 px-2 py-0.5 text-[11px] font-medium text-amber-400 border border-amber-500/20">
              {media.status}
            </span>
          )}
          {isFailed && (
            <span className="inline-flex items-center rounded-full bg-rose-500/10 px-2 py-0.5 text-[11px] font-medium text-rose-400 border border-rose-500/20">
              Failed
            </span>
          )}
        </div>
      </div>
    </div>
  );
};
