import { useState, useEffect } from 'react';

interface BrowserViewProps {
  url: string;
  isLoading: boolean;
  isFullscreen: boolean;
  onBack: () => void;
  onClose: () => void;
  onLoad: () => void;
  onToggleFullscreen: () => void;
  blockedCount: number;
}

export default function BrowserView({
  url,
  isLoading,
  isFullscreen,
  onBack,
  onClose,
  onLoad,
  onToggleFullscreen,
  blockedCount,
}: BrowserViewProps) {
  const [progress, setProgress] = useState(0);
  const [showUrlBar, setShowUrlBar] = useState(true);

  useEffect(() => {
    if (isLoading) {
      setProgress(0);
      const interval = setInterval(() => {
        setProgress(prev => {
          if (prev >= 90) {
            clearInterval(interval);
            return 90;
          }
          return prev + Math.random() * 15;
        });
      }, 100);
      return () => clearInterval(interval);
    } else {
      setProgress(100);
      const timeout = setTimeout(() => setProgress(0), 500);
      return () => clearTimeout(timeout);
    }
  }, [isLoading]);

  const displayUrl = url.replace(/^https?:\/\//, '').replace(/\/$/, '');

  return (
    <div className="flex-1 flex flex-col overflow-hidden relative">
      {/* Top bar */}
      {!isFullscreen && (
        <div className="bg-[#1a1a1a] border-b border-[#2a2a2a]">
          {/* Progress bar */}
          {progress > 0 && progress < 100 && (
            <div className="absolute top-0 left-0 right-0 h-0.5 bg-[#2a2a2a] z-10">
              <div
                className="h-full bg-emerald-500 transition-all duration-200 ease-out"
                style={{ width: `${progress}%` }}
              />
            </div>
          )}

          {/* Navigation bar */}
          <div className="flex items-center gap-2 px-3 py-2">
            {/* Back button */}
            <button
              onClick={onBack}
              className="w-9 h-9 flex items-center justify-center rounded-lg hover:bg-[#2a2a2a] transition-colors"
              title="Atrás (ESC)"
            >
              <svg className="w-5 h-5 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18"/>
              </svg>
            </button>

            {/* URL display */}
            <div className="flex-1 flex items-center gap-2 bg-[#0f0f0f] rounded-lg px-3 py-2 border border-[#2a2a2a]">
              <div className="flex items-center gap-1.5">
                <svg className="w-3.5 h-3.5 text-emerald-500" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clipRule="evenodd"/>
                </svg>
              </div>
              <span className="text-sm text-gray-300 truncate flex-1 font-mono">{displayUrl}</span>
              {blockedCount > 0 && (
                <div className="flex items-center gap-1 bg-emerald-500/10 px-2 py-0.5 rounded-full">
                  <svg className="w-3 h-3 text-emerald-400" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                  </svg>
                  <span className="text-[10px] text-emerald-400 font-medium">{blockedCount}</span>
                </div>
              )}
            </div>

            {/* Fullscreen button */}
            <button
              onClick={onToggleFullscreen}
              className="w-9 h-9 flex items-center justify-center rounded-lg hover:bg-[#2a2a2a] transition-colors"
              title="Pantalla completa"
            >
              <svg className="w-5 h-5 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4"/>
              </svg>
            </button>

            {/* Close button */}
            <button
              onClick={onClose}
              className="w-9 h-9 flex items-center justify-center rounded-lg hover:bg-red-500/20 transition-colors"
              title="Cerrar"
            >
              <svg className="w-5 h-5 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12"/>
              </svg>
            </button>
          </div>
        </div>
      )}

      {/* Iframe container */}
      <div className="flex-1 relative bg-black">
        {/* Loading overlay */}
        {isLoading && (
          <div className="absolute inset-0 flex items-center justify-center bg-[#0f0f0f] z-10">
            <div className="flex flex-col items-center gap-4">
              <div className="relative">
                <div className="w-12 h-12 border-2 border-[#2a2a2a] rounded-full"></div>
                <div className="absolute inset-0 w-12 h-12 border-2 border-emerald-500 border-t-transparent rounded-full animate-spin"></div>
              </div>
              <p className="text-sm text-gray-400">Cargando contenido...</p>
              <div className="flex items-center gap-2 text-xs text-gray-500">
                <svg className="w-3 h-3 text-emerald-500" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M2.166 4.999A11.954 11.954 0 0010 1.944 11.954 11.954 0 0017.834 5c.11.65.166 1.32.166 2.001 0 5.225-3.34 9.67-8 11.317C5.34 16.67 2 12.225 2 7c0-.682.057-1.35.166-2.001zm11.541 3.708a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd"/>
                </svg>
                <span>Bloqueando anuncios en tiempo real</span>
              </div>
            </div>
          </div>
        )}

        {/* The iframe */}
        <iframe
          src={url}
          className="w-full h-full border-0"
          onLoad={onLoad}
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; fullscreen"
          allowFullScreen
          sandbox="allow-same-origin allow-scripts allow-popups allow-forms allow-presentation"
          title="Video Player"
        />

        {/* Fullscreen exit button */}
        {isFullscreen && (
          <button
            onClick={onToggleFullscreen}
            className="absolute top-4 right-4 w-10 h-10 bg-black/70 backdrop-blur-sm rounded-full flex items-center justify-center hover:bg-black/90 transition-colors z-20"
          >
            <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 9V4.5M9 9H4.5M9 9L3.75 3.75M9 15v4.5M9 15H4.5M9 15l-5.25 5.25M15 9h4.5M15 9V4.5M15 9l5.25-5.25M15 15h4.5M15 15v4.5m0-4.5l5.25 5.25"/>
            </svg>
          </button>
        )}
      </div>

      {/* Bottom bar */}
      {!isFullscreen && (
        <div className="bg-[#1a1a1a] border-t border-[#2a2a2a] px-4 py-2 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="w-2 h-2 rounded-full bg-emerald-500"></div>
            <span className="text-xs text-gray-400">Extensiones activas</span>
          </div>
          <div className="flex items-center gap-3">
            <div className="flex items-center gap-1 text-xs text-gray-500">
              <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
              </svg>
              <span>AdBlock ON</span>
            </div>
            <div className="flex items-center gap-1 text-xs text-gray-500">
              <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd"/>
              </svg>
              <span>PopUp Block</span>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
