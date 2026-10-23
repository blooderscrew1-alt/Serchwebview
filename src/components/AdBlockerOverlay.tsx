import { useEffect, useState } from 'react';

interface AdBlockerOverlayProps {
  blockedCount: number;
}

export default function AdBlockerOverlay({ blockedCount }: AdBlockerOverlayProps) {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    setIsVisible(true);
    const timer = setTimeout(() => setIsVisible(false), 1800);
    return () => clearTimeout(timer);
  }, []);

  return (
    <div
      className={`absolute top-14 left-1/2 -translate-x-1/2 z-50 transition-all duration-300 ${
        isVisible ? 'opacity-100 translate-y-0' : 'opacity-0 -translate-y-4'
      }`}
    >
      <div className="flex items-center gap-2 bg-[#1a1a1a] border border-emerald-500/30 rounded-xl px-4 py-2.5 shadow-lg shadow-black/50 backdrop-blur-sm">
        <div className="w-7 h-7 bg-emerald-500/20 rounded-lg flex items-center justify-center">
          <svg className="w-4 h-4 text-emerald-400" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
          </svg>
        </div>
        <div>
          <p className="text-xs font-medium text-white">
            {blockedCount} {blockedCount === 1 ? 'anuncio bloqueado' : 'anuncios bloqueados'}
          </p>
          <p className="text-[10px] text-gray-400">Pop-ups y scripts bloqueados</p>
        </div>
        <div className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></div>
      </div>
    </div>
  );
}
