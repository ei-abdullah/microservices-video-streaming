import React from 'react';
import { Upload, LogIn, LogOut } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

interface NavbarProps {
  onOpenUpload: () => void;
  onOpenAuth: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ onOpenUpload, onOpenAuth }) => {
  const { isAuthenticated, logout } = useAuth();

  return (
    <header className="sticky top-0 z-40 w-full border-b border-zinc-800 bg-black">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">
        {/* Brand */}
        <span className="text-sm font-semibold tracking-widest text-white uppercase">
          Stream
        </span>

        {/* Actions */}
        <div className="flex items-center gap-3">
          <button
            onClick={onOpenUpload}
            className="flex items-center gap-2 rounded-none border border-white px-4 py-2 text-xs font-medium text-white transition hover:bg-white hover:text-black cursor-pointer"
          >
            <Upload className="h-3.5 w-3.5" />
            <span>Upload</span>
          </button>

          {isAuthenticated ? (
            <button
              onClick={logout}
              className="flex items-center gap-1.5 px-3 py-2 text-xs font-medium text-zinc-400 transition hover:text-white cursor-pointer"
              title="Sign out"
            >
              <LogOut className="h-3.5 w-3.5" />
              <span className="hidden sm:inline">Sign out</span>
            </button>
          ) : (
            <button
              onClick={onOpenAuth}
              className="flex items-center gap-1.5 px-3 py-2 text-xs font-medium text-zinc-400 transition hover:text-white cursor-pointer"
            >
              <LogIn className="h-3.5 w-3.5" />
              <span>Sign in</span>
            </button>
          )}
        </div>
      </div>
    </header>
  );
};
