import { useRef, useState } from 'react';
import Layout from '../../layouts/Layout';
import { Html5QrcodeScanner } from 'html5-qrcode';
import { useEffect } from 'react';
import api from '../../api/axios';
import toast from 'react-hot-toast';

export default function QRPage() {
  const [scanResult, setScanResult] = useState(null);
  const [scanError, setScanError] = useState(null);
  const [scanning, setScanning] = useState(false);
  const scannerRef = useRef(null);

  useEffect(() => {
    return () => {
      if (scannerRef.current) {
        scannerRef.current.clear().catch(() => {});
      }
    };
  }, []);

  const startScanner = () => {
    setScanning(true);
    setScanResult(null);
    setScanError(null);

    const scanner = new Html5QrcodeScanner('qr-reader', {
      fps: 10,
      qrbox: { width: 250, height: 250 },
    }, false);

    scanner.render(
      (decodedText) => {
        setScanResult(decodedText);
        scanner.clear().catch(() => {});
        setScanning(false);
        // Mark attendance with scanned QR
        api.post('/api/attendance/mark', { qrData: decodedText })
          .then(() => toast.success('Attendance marked successfully!'))
          .catch(err => toast.error(err.response?.data?.message || 'Failed to mark attendance'));
      },
      (errorMsg) => {
        // Ignore continuous not-found messages
        if (!errorMsg.includes('No QR code found')) {
          setScanError(errorMsg);
        }
      }
    );
    scannerRef.current = scanner;
  };

  return (
    <Layout>
      <header className="dash-header">
        <div>
          <h1>QR Scanner</h1>
          <p>Scan QR codes to mark student attendance</p>
        </div>
      </header>

      <div className="qr-container">
        <div className="page-card qr-card">
          <h2>Scan Student QR Code</h2>
          <p className="qr-sub">Click the button below to activate your camera and scan</p>

          {!scanning && !scanResult && (
            <button className="btn-primary btn-lg" onClick={startScanner}>
              📷 Start Camera Scanner
            </button>
          )}

          <div id="qr-reader" style={{ width: '100%', maxWidth: 400, margin: '0 auto' }}></div>

          {scanResult && (
            <div className="scan-success">
              <p className="success-icon">✅</p>
              <p className="success-title">QR Code Scanned!</p>
              <code className="scan-data">{scanResult}</code>
              <button className="btn-primary" onClick={startScanner}>Scan Another</button>
            </div>
          )}

          {scanError && (
            <div className="scan-error">
              <p>⚠️ {scanError}</p>
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
}
