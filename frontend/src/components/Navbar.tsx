import React from 'react';
import { PlaySquare, Upload, LogIn, LogOut, CheckCircle2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

interface NavbarProps {
  onOpenUpload: () => void;
  onOpenAuth: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ onOpenUpload, onOpenAuth }) => {
  const { isAuthenticated, logout } = useAuth();

  return (
    <header className="sticky top-0 z-40 w-full border-b border-slate-800 bg-slate-950/80 backdrop-blur-md">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3 sm:px-6">
        {/* Brand */}
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-tr from-indigo-600 to-violet-500 shadow-lg shadow-indigo-500/30">
            <PlaySquare className="h-6 w-6 text-white" />
          </div>
        </div>

        {/* Action Controls */}
        <div className="flex items-center gap-3">
          <button
            onClick={onOpenUpload}
            className="flex items-center gap-2 rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-500 active:scale-95 shadow-md shadow-indigo-600/20 cursor-pointer"
          >
            <Upload className="h-4 w-4" />
            <span>Upload Video</span>
          </button>

          {isAuthenticated ? (
            <div className="flex items-center gap-3">
              <span className="hidden items-center gap-1.5 text-xs font-medium text-emerald-400 bg-emerald-500/10 border border-emerald-500/20 px-2.5 py-1 rounded-full sm:flex">
                <CheckCircle2 className="h-3.5 w-3.5" />
                Verified Creator
              </span>
              <button
                onClick={logout}
                className="flex items-center gap-1.5 rounded-lg border border-slate-800 bg-slate-900 px-3 py-2 text-sm font-medium text-slate-300 transition hover:bg-slate-800 hover:text-white cursor-pointer"
                title="Sign out"
              >
                <LogOut className="h-4 w-4" />
                <span className="hidden sm:inline">Logout</span>
              </button>
            </div>
          ) : (
            <button
              onClick={onOpenAuth}
              className="flex items-center gap-1.5 rounded-lg border border-slate-700 bg-slate-800 px-4 py-2 text-sm font-medium text-white transition hover:bg-slate-700 active:scale-95 cursor-pointer"
            >
              <LogIn className="h-4 w-4" />
              <span>Sign In</span>
            </button>
          )}
        </div>
      </div>
    </header>
  );
};
