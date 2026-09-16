import React, { useState, useEffect } from 'react';
import { Navbar } from './components/Navbar';
import { VideoPlayer } from './components/VideoPlayer';
import { VideoCard } from './components/VideoCard';
import { UploadModal } from './components/UploadModal';
import { AuthModal } from './components/AuthModal';
import { mediaApi } from './api/mediaApi';
import type { Media } from './types/media';
import { Film, RefreshCw } from 'lucide-react';

export const App: React.FC = () => {
  const [mediaList, setMediaList] = useState<Media[]>([]);
  const [selectedMedia, setSelectedMedia] = useState<Media | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [isUploadOpen, setIsUploadOpen] = useState<boolean>(false);
  const [isAuthOpen, setIsAuthOpen] = useState<boolean>(false);

  const fetchVideos = async () => {
    try {
      setIsLoading(true);
      const data = await mediaApi.getAllMedia();
      setMediaList(data);

      setSelectedMedia((prev) => {
        if (!prev && data.length > 0) {
          return data.find((m) => m.status === 'READY') || null;
        }
        return prev;
      });
    } catch (err) {
      console.error('Failed to fetch media list:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    let ignore = false;

    mediaApi.getAllMedia().then((data) => {
      if (!ignore) {
        setMediaList(data);
        const firstReady = data.find((m) => m.status === 'READY');
        if (firstReady) {
          setSelectedMedia(firstReady);
        }
        setIsLoading(false);
      }
    }).catch((err) => {
      if (!ignore) {
        console.error('Failed to fetch media list:', err);
        setIsLoading(false);
      }
    });

    return () => {
      ignore = true;
    };
  }, []);

  // Background auto-refresh if any media is still processing
  useEffect(() => {
    const hasProcessing = mediaList.some(
      (m) => m.status !== 'READY' && m.status !== 'FAILED'
    );

    if (!hasProcessing) return;

    const interval = setInterval(async () => {
      try {
        const data = await mediaApi.getAllMedia();
        setMediaList(data);
      } catch {
        // Silent catch for background polling
      }
    }, 4000);

    return () => clearInterval(interval);
  }, [mediaList]);

  return (
    <div className="min-h-screen bg-black text-white">
      <Navbar
        onOpenUpload={() => setIsUploadOpen(true)}
        onOpenAuth={() => setIsAuthOpen(true)}
      />

      <main className="mx-auto max-w-7xl px-6 py-10 space-y-12">
        {/* Player */}
        {selectedMedia && selectedMedia.playbackUrl && (
          <section className="space-y-3">
            <div className="flex items-center justify-between">
              <p className="text-xs text-zinc-500 uppercase tracking-widest">Now playing</p>
              <span className="text-xs text-zinc-700 font-mono">{selectedMedia.id}</span>
            </div>
            <VideoPlayer
              src={selectedMedia.playbackUrl}
              title={selectedMedia.title || 'Untitled'}
            />
          </section>
        )}

        {/* Library */}
        <section className="space-y-5">
          <div className="flex items-center justify-between border-b border-zinc-900 pb-4">
            <h2 className="text-sm font-medium text-white">Library</h2>

            <button
              onClick={fetchVideos}
              className="flex items-center gap-1.5 px-3 py-1.5 text-xs text-zinc-500 hover:text-white transition cursor-pointer"
            >
              <RefreshCw className={`h-3 w-3 ${isLoading ? 'animate-spin' : ''}`} />
              <span>Refresh</span>
            </button>
          </div>

          {mediaList.length > 0 ? (
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
              {mediaList.map((media) => (
                <VideoCard
                  key={media.id}
                  media={media}
                  isSelected={selectedMedia?.id === media.id}
                  onSelect={(m) => setSelectedMedia(m)}
                />
              ))}
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center border border-dashed border-zinc-800 py-20 text-center">
              <Film className="h-8 w-8 text-zinc-700 mb-3" />
              <p className="text-sm text-zinc-500">No videos yet.</p>
              <button
                onClick={() => setIsUploadOpen(true)}
                className="mt-4 border border-zinc-700 px-4 py-2 text-xs text-zinc-400 hover:border-white hover:text-white transition cursor-pointer"
              >
                Upload your first video
              </button>
            </div>
          )}
        </section>
      </main>

      <UploadModal
        isOpen={isUploadOpen}
        onClose={() => setIsUploadOpen(false)}
        onUploadSuccess={fetchVideos}
      />

      <AuthModal
        isOpen={isAuthOpen}
        onClose={() => setIsAuthOpen(false)}
      />
    </div>
  );
};

export default App;
