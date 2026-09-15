import React, {useState, useEffect} from 'react';
import {Navbar} from './components/Navbar';
import {VideoPlayer} from './components/VideoPlayer';
import {VideoCard} from './components/VideoCard';
import {UploadModal} from './components/UploadModal';
import {AuthModal} from './components/AuthModal';
import {mediaApi} from './api/mediaApi';
import type {Media} from './types/media';
import {Film, RefreshCw, Layers, Sparkles} from 'lucide-react';

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
        <div className="min-h-screen bg-slate-950 text-slate-100 selection:bg-indigo-500 selection:text-white">
            {/* Navigation Header */}
            <Navbar
                onOpenUpload={() => setIsUploadOpen(true)}
                onOpenAuth={() => setIsAuthOpen(true)}
            />

            <main className="mx-auto max-w-7xl px-4 py-8 sm:px-6 space-y-10">
                {/* Active Cinema Stream Section */}
                {selectedMedia && selectedMedia.playbackUrl && (
                    <section className="space-y-4">
                        <div className="flex items-center justify-between">
                            <h2 className="text-lg font-bold text-slate-200 flex items-center gap-2">
                                <Sparkles className="h-5 w-5 text-indigo-400"/>
                                Now Streaming
                            </h2>
                            <span className="text-xs text-slate-400 font-mono">
                ID: {selectedMedia.id}
              </span>
                        </div>

                        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                            {/* Main Player */}
                            <div className="lg:col-span-2">
                                <VideoPlayer
                                    src={selectedMedia.playbackUrl}
                                    title={selectedMedia.title || `Video #${selectedMedia.id.slice(0, 8)}`}
                                />
                            </div>

                            {/* Stream Specs & Architecture Card */}
                            <div
                                className="rounded-2xl border border-slate-800 bg-slate-900/50 p-5 flex flex-col justify-between">
                                <div>
                                    <h3 className="text-base font-bold text-white mb-3">HLS Adaptive Manifest</h3>
                                    <div className="space-y-2 text-xs">
                                        <div className="flex justify-between py-1.5 border-b border-slate-800">
                                            <span className="text-slate-400">Stream Protocol</span>
                                            <span className="font-mono text-indigo-400 font-semibold">HTTP Live Streaming (HLS)</span>
                                        </div>
                                        <div className="flex justify-between py-1.5 border-b border-slate-800">
                                            <span className="text-slate-400">Resolution</span>
                                            <span
                                                className="font-mono text-slate-200">{selectedMedia.width}x{selectedMedia.height}</span>
                                        </div>
                                        <div className="flex justify-between py-1.5 border-b border-slate-800">
                                            <span className="text-slate-400">Duration</span>
                                            <span
                                                className="font-mono text-slate-200">{selectedMedia.duration ? `${Math.round(selectedMedia.duration)}s` : 'N/A'}</span>
                                        </div>
                                        <div className="flex justify-between py-1.5 border-b border-slate-800">
                                            <span className="text-slate-400">Processing Node</span>
                                            <span
                                                className="font-mono text-emerald-400">transcoding-service :8400</span>
                                        </div>
                                        <div className="flex justify-between py-1.5 border-b border-slate-800">
                                            <span className="text-slate-400">Delivery Route</span>
                                            <span className="font-mono text-indigo-400">api-gateway :8080</span>
                                        </div>
                                    </div>

                                    <div className="mt-4">
                                        <span className="text-[11px] font-semibold text-slate-400 block mb-1">Raw HLS Manifest URI</span>
                                        <input
                                            readOnly
                                            value={selectedMedia.playbackUrl}
                                            className="w-full rounded-md border border-slate-800 bg-slate-950 px-2 py-1.5 text-[11px] font-mono text-slate-300 select-all outline-none"
                                        />
                                    </div>
                                </div>

                                <div
                                    className="mt-6 rounded-xl bg-indigo-500/10 border border-indigo-500/20 p-3 text-xs text-indigo-300">
                                    Transcoded via libx264 + AAC into 4-second .ts segments with index.m3u8 playlist.
                                </div>
                            </div>
                        </div>
                    </section>
                )}

                {/* Video Library Feed Section */}
                <section className="space-y-6">
                    <div className="flex items-center justify-between border-b border-slate-800 pb-4">
                        <div className="flex items-center gap-3">
                            <div
                                className="flex h-8 w-8 items-center justify-center rounded-lg bg-slate-800 text-indigo-400">
                                <Layers className="h-4 w-4"/>
                            </div>
                            <div>
                                <h2 className="text-lg font-bold text-white">Video Library</h2>
                                <p className="text-xs text-slate-400">All media ingested and distributed across the
                                    cluster</p>
                            </div>
                        </div>

                        <button
                            onClick={fetchVideos}
                            className="flex items-center gap-1.5 rounded-lg border border-slate-800 bg-slate-900 px-3 py-1.5 text-xs font-medium text-slate-300 hover:bg-slate-800 hover:text-white transition cursor-pointer"
                        >
                            <RefreshCw className={`h-3.5 w-3.5 ${isLoading ? 'animate-spin' : ''}`}/>
                            <span>Refresh</span>
                        </button>
                    </div>

                    {/* Video Grid */}
                    {mediaList.length > 0 ? (
                        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
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
                        <div
                            className="flex flex-col items-center justify-center rounded-2xl border border-dashed border-slate-800 bg-slate-900/30 py-16 text-center">
                            <Film className="h-12 w-12 text-slate-600 mb-3"/>
                            <h3 className="text-base font-semibold text-slate-200">No Videos Found</h3>
                            <p className="text-xs text-slate-500 mt-1 max-w-sm">
                                Get started by uploading an MP4 video file. It will be automatically queued and
                                transcoded to HLS.
                            </p>
                            <button
                                onClick={() => setIsUploadOpen(true)}
                                className="mt-5 rounded-lg bg-indigo-600 px-4 py-2 text-xs font-semibold text-white hover:bg-indigo-500 transition shadow-md shadow-indigo-600/20 cursor-pointer"
                            >
                                Upload First Video
                            </button>
                        </div>
                    )}
                </section>
            </main>

            {/* Upload and Auth Modals */}
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
