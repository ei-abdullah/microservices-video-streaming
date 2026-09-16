import React, { useState } from 'react';
import { X, Loader2, AlertCircle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

interface AuthModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const AuthModal: React.FC<AuthModalProps> = ({ isOpen, onClose }) => {
  const { login, signup } = useAuth();
  const [isLogin, setIsLogin] = useState<boolean>(true);
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !password) return;

    setIsLoading(true);
    setError(null);

    try {
      if (isLogin) {
        await login(email, password);
      } else {
        await signup(email, password);
      }
      onClose();
    } catch (err: unknown) {
      let msg = 'Authentication failed. Please check your credentials.';
      if (err instanceof Error) {
        msg = err.message;
      }
      setError(msg);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4">
      <div className="relative w-full max-w-sm bg-zinc-950 border border-zinc-800 p-8">
        <button
          onClick={onClose}
          className="absolute right-4 top-4 p-1 text-zinc-500 hover:text-white transition cursor-pointer"
        >
          <X className="h-4 w-4" />
        </button>

        {/* Tab switcher */}
        <div className="flex border-b border-zinc-800 mb-8">
          <button
            type="button"
            onClick={() => { setIsLogin(true); setError(null); }}
            className={`flex-1 pb-3 text-xs font-medium transition cursor-pointer border-b-2 -mb-px ${
              isLogin ? 'border-white text-white' : 'border-transparent text-zinc-500 hover:text-white'
            }`}
          >
            Sign in
          </button>
          <button
            type="button"
            onClick={() => { setIsLogin(false); setError(null); }}
            className={`flex-1 pb-3 text-xs font-medium transition cursor-pointer border-b-2 -mb-px ${
              !isLogin ? 'border-white text-white' : 'border-transparent text-zinc-500 hover:text-white'
            }`}
          >
            Create account
          </button>
        </div>

        {error && (
          <div className="mb-5 flex items-center gap-2 p-3 border border-zinc-700 text-xs text-zinc-400">
            <AlertCircle className="h-3.5 w-3.5 shrink-0" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs text-zinc-400 mb-1.5">Email</label>
            <input
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="you@example.com"
              className="w-full border border-zinc-800 bg-black py-2.5 px-3 text-sm text-white placeholder-zinc-600 outline-none focus:border-zinc-600 transition"
            />
          </div>

          <div>
            <label className="block text-xs text-zinc-400 mb-1.5">Password</label>
            <input
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              className="w-full border border-zinc-800 bg-black py-2.5 px-3 text-sm text-white placeholder-zinc-600 outline-none focus:border-zinc-600 transition"
            />
          </div>

          <button
            type="submit"
            disabled={isLoading}
            className="w-full flex items-center justify-center gap-2 bg-white py-2.5 text-xs font-semibold text-black transition hover:bg-zinc-200 disabled:opacity-50 cursor-pointer mt-2"
          >
            {isLoading && <Loader2 className="h-3.5 w-3.5 animate-spin" />}
            <span>{isLogin ? 'Sign in' : 'Create account'}</span>
          </button>
        </form>
      </div>
    </div>
  );
};
