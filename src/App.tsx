import { useState, useEffect, useRef, useCallback } from 'react';
import BrowserView from './components/BrowserView';
import HomeScreen from './components/HomeScreen';
import AdBlockerOverlay from './components/AdBlockerOverlay';

function App() {
  const [currentUrl, setCurrentUrl] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false);
  const [adBlocked, setAdBlocked] = useState(false);
  const [blockedCount, setBlockedCount] = useState(0);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [history, setHistory] = useState<string[]>([]);
  const [showExitConfirm, setShowExitConfirm] = useState(false);

  const handleUrlSubmit = useCallback((url: string) => {
    let processedUrl = url.trim();
    
    // Add https:// if no protocol
    if (!processedUrl.startsWith('http://') && !processedUrl.startsWith('https://')) {
      processedUrl = 'https://' + processedUrl;
    }

    setHistory(prev => [...prev, currentUrl]);
    setCurrentUrl(processedUrl);
    setIsLoading(true);
    setAdBlocked(false);

    // Simulate ad blocking
    const adsDetected = Math.floor(Math.random() * 5) + 1;
    setTimeout(() => {
      setBlockedCount(prev => prev + adsDetected);
      setAdBlocked(true);
      setTimeout(() => setAdBlocked(false), 2000);
    }, 500);
  }, [currentUrl]);

  const handleBack = useCallback(() => {
    if (history.length > 0) {
      const prevUrl = history[history.length - 1];
      setHistory(prev => prev.slice(0, -1));
      setCurrentUrl(prevUrl);
      setIsLoading(true);
    } else {
      setShowExitConfirm(true);
    }
  }, [history]);

  const handleClose = useCallback(() => {
    setCurrentUrl('');
    setHistory([]);
    setIsLoading(false);
    setIsFullscreen(false);
  }, []);

  const handleIframeLoad = useCallback(() => {
    setIsLoading(false);
  }, []);

  const toggleFullscreen = useCallback(() => {
    setIsFullscreen(prev => !prev);
  }, []);

  // Handle keyboard back button / escape
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        if (isFullscreen) {
          setIsFullscreen(false);
        } else if (currentUrl) {
          handleBack();
        }
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [currentUrl, isFullscreen, handleBack]);

  // Handle browser back button
  useEffect(() => {
    const handlePopState = () => {
      if (currentUrl) {
        handleBack();
      }
    };
    window.addEventListener('popstate', handlePopState);
    return () => window.removeEventListener('popstate', handlePopState);
  }, [currentUrl, handleBack]);

  return (
    <div className="h-screen w-screen overflow-hidden bg-[#0f0f0f] text-white flex flex-col relative">
      {/* Status bar simulation */}
      {!isFullscreen && (
        <div className="flex items-center justify-between px-4 py-1.5 bg-[#1a1a1a] border-b border-[#2a2a2a] text-xs text-gray-400">
          <div className="flex items-center gap-2">
            <div className="w-2 h-2 rounded-full bg-green-500 animate-pulse"></div>
            <span className="font-medium">SwiftView</span>
          </div>
          <div className="flex items-center gap-3">
            <span className="flex items-center gap-1">
              <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
                <path d="M2 11a1 1 0 011-1h2a1 1 0 011 1v5a1 1 0 01-1 1H3a1 1 0 01-1-1v-5zm6-4a1 1 0 011-1h2a1 1 0 011 1v9a1 1 0 01-1 1H9a1 1 0 01-1-1V7zm6-3a1 1 0 011-1h2a1 1 0 011 1v12a1 1 0 01-1 1h-2a1 1 0 01-1-1V4z"/>
              </svg>
              100%
            </span>
            <span>12:00</span>
          </div>
        </div>
      )}

      {/* Ad blocker notification */}
      {adBlocked && <AdBlockerOverlay blockedCount={blockedCount} />}

      {/* Main content */}
      {currentUrl ? (
        <BrowserView
          url={currentUrl}
          isLoading={isLoading}
          isFullscreen={isFullscreen}
          onBack={handleBack}
          onClose={handleClose}
          onLoad={handleIframeLoad}
          onToggleFullscreen={toggleFullscreen}
          blockedCount={blockedCount}
        />
      ) : (
        <HomeScreen onUrlSubmit={handleUrlSubmit} blockedCount={blockedCount} />
      )}

      {/* Exit confirmation modal */}
      {showExitConfirm && (
        <div className="absolute inset-0 bg-black/80 flex items-center justify-center z-50 backdrop-blur-sm">
          <div className="bg-[#1e1e1e] rounded-2xl p-6 mx-6 max-w-sm w-full border border-[#333] shadow-2xl">
            <div className="text-center mb-4">
              <div className="w-12 h-12 bg-[#2a2a2a] rounded-full flex items-center justify-center mx-auto mb-3">
                <svg className="w-6 h-6 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
                </svg>
              </div>
              <h3 className="text-lg font-semibold text-white">¿Salir de SwiftView?</h3>
              <p className="text-sm text-gray-400 mt-1">Se cerrará el navegador</p>
            </div>
            <div className="flex gap-3">
              <button
                onClick={() => setShowExitConfirm(false)}
                className="flex-1 py-3 rounded-xl bg-[#2a2a2a] text-white font-medium hover:bg-[#333] transition-colors"
              >
                Cancelar
              </button>
              <button
                onClick={() => {
                  setShowExitConfirm(false);
                  handleClose();
                }}
                className="flex-1 py-3 rounded-xl bg-red-600 text-white font-medium hover:bg-red-700 transition-colors"
              >
                Salir
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
