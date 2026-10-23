import { useState, useRef, useEffect } from 'react';

interface HomeScreenProps {
  onUrlSubmit: (url: string) => void;
  blockedCount: number;
}

const quickLinks = [
  { name: 'YouTube', url: 'youtube.com', icon: '▶️', color: 'from-red-600 to-red-800' },
  { name: 'Twitch', url: 'twitch.tv', icon: '🎮', color: 'from-purple-600 to-purple-800' },
  { name: 'Vimeo', url: 'vimeo.com', icon: '🎬', color: 'from-blue-600 to-blue-800' },
  { name: 'Dailymotion', url: 'dailymotion.com', icon: '📺', color: 'from-sky-600 to-sky-800' },
];

export default function HomeScreen({ onUrlSubmit, blockedCount }: HomeScreenProps) {
  const [url, setUrl] = useState('');
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    inputRef.current?.focus();
  }, []);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (url.trim()) {
      onUrlSubmit(url);
    }
  };

  const handleQuickLink = (linkUrl: string) => {
    onUrlSubmit(linkUrl);
  };

  return (
    <div className="flex-1 flex flex-col items-center justify-center px-6 py-8">
      {/* Logo */}
      <div className="mb-8 text-center">
        <div className="relative inline-block">
          <div className="w-20 h-20 rounded-2xl bg-gradient-to-br from-emerald-500 to-teal-700 flex items-center justify-center shadow-lg shadow-emerald-500/20">
            <svg className="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z"/>
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <div className="absolute -top-1 -right-1 w-5 h-5 bg-red-500 rounded-full flex items-center justify-center">
            <svg className="w-3 h-3 text-white" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
            </svg>
          </div>
        </div>
        <h1 className="text-2xl font-bold mt-4 text-white">SwiftView</h1>
        <p className="text-sm text-gray-400 mt-1">Navegador de video ultra ligero</p>
      </div>

      {/* URL Input */}
      <form onSubmit={handleSubmit} className="w-full max-w-md mb-6">
        <div className="relative">
          <div className="absolute left-4 top-1/2 -translate-y-1/2">
            <svg className="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1"/>
            </svg>
          </div>
          <input
            ref={inputRef}
            type="text"
            value={url}
            onChange={(e) => setUrl(e.target.value)}
            placeholder="Pega enlace de video aquí..."
            className="w-full pl-12 pr-14 py-4 bg-[#1e1e1e] border border-[#333] rounded-2xl text-white placeholder-gray-500 focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 transition-all text-base"
          />
          <button
            type="submit"
            disabled={!url.trim()}
            className="absolute right-2 top-1/2 -translate-y-1/2 w-10 h-10 bg-emerald-600 hover:bg-emerald-500 disabled:bg-[#333] disabled:text-gray-600 rounded-xl flex items-center justify-center transition-all"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 5l7 7m0 0l-7 7m7-7H3"/>
            </svg>
          </button>
        </div>
      </form>

      {/* Quick Links */}
      <div className="w-full max-w-md">
        <p className="text-xs text-gray-500 uppercase tracking-wider mb-3 px-1">Acceso rápido</p>
        <div className="grid grid-cols-2 gap-3">
          {quickLinks.map((link) => (
            <button
              key={link.name}
              onClick={() => handleQuickLink(link.url)}
              className="flex items-center gap-3 p-4 bg-[#1e1e1e] border border-[#2a2a2a] rounded-xl hover:border-[#444] transition-all group"
            >
              <div className={`w-10 h-10 rounded-lg bg-gradient-to-br ${link.color} flex items-center justify-center text-lg shadow-sm`}>
                {link.icon}
              </div>
              <span className="text-sm font-medium text-gray-300 group-hover:text-white transition-colors">{link.name}</span>
            </button>
          ))}
        </div>
      </div>

      {/* Ad blocker stats */}
      <div className="mt-8 flex items-center gap-2 text-xs text-gray-500">
        <svg className="w-4 h-4 text-emerald-500" fill="currentColor" viewBox="0 0 20 20">
          <path fillRule="evenodd" d="M2.166 4.999A11.954 11.954 0 0010 1.944 11.954 11.954 0 0017.834 5c.11.65.166 1.32.166 2.001 0 5.225-3.34 9.67-8 11.317C5.34 16.67 2 12.225 2 7c0-.682.057-1.35.166-2.001zm11.541 3.708a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd"/>
        </svg>
        <span>Bloqueador activo · {blockedCount} anuncios bloqueados</span>
      </div>

      {/* Features */}
      <div className="mt-6 grid grid-cols-3 gap-4 text-center w-full max-w-md">
        <div className="flex flex-col items-center gap-1">
          <div className="w-8 h-8 rounded-lg bg-[#1e1e1e] border border-[#2a2a2a] flex items-center justify-center">
            <svg className="w-4 h-4 text-emerald-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z"/>
            </svg>
          </div>
          <span className="text-[10px] text-gray-500">Ultra rápido</span>
        </div>
        <div className="flex flex-col items-center gap-1">
          <div className="w-8 h-8 rounded-lg bg-[#1e1e1e] border border-[#2a2a2a] flex items-center justify-center">
            <svg className="w-4 h-4 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636"/>
            </svg>
          </div>
          <span className="text-[10px] text-gray-500">Sin anuncios</span>
        </div>
        <div className="flex flex-col items-center gap-1">
          <div className="w-8 h-8 rounded-lg bg-[#1e1e1e] border border-[#2a2a2a] flex items-center justify-center">
            <svg className="w-4 h-4 text-purple-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 5a1 1 0 011-1h14a1 1 0 011 1v2a1 1 0 01-1 1H5a1 1 0 01-1-1V5zM4 13a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H5a1 1 0 01-1-1v-6z"/>
            </svg>
          </div>
          <span className="text-[10px] text-gray-500">Extensiones</span>
        </div>
      </div>
    </div>
  );
}
