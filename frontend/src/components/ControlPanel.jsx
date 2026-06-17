import React, { useState, useEffect } from 'react';
import { setColor, setEngine, setSpeed, saveGame, loadGame } from '../api';

const ControlPanel = ({ gameState, activeEntityId }) => {
  const [localColor, setLocalColor] = useState('#3498db');
  const [localEngine, setLocalEngine] = useState('FUEL');
  const [localSpeed, setLocalSpeed] = useState(80);
  const [lastManualChange, setLastManualChange] = useState(0);

  // Update local state when backend pushes changes
  useEffect(() => {
    if (gameState) {
      if (gameState.color) setLocalColor(gameState.color);
      
      const now = Date.now();
      if (now - lastManualChange > 500) {
        if (gameState.engine) setLocalEngine(String(gameState.engine).toUpperCase());
        if (gameState.speed !== undefined) setLocalSpeed(gameState.speed);
      }
    }
  }, [gameState, lastManualChange]);

  const handleColorChange = (e) => {
    const val = e.target.value;
    setLastManualChange(Date.now());
    setLocalColor(val);
    if (activeEntityId !== null) setColor(val, activeEntityId).catch(console.error);
  };

  const handleEngineChange = (e) => {
    const val = e.target.value;
    setLastManualChange(Date.now());
    setLocalEngine(val);
    if (activeEntityId !== null) setEngine(val, activeEntityId).catch(console.error);
  };

  const handleSpeedChange = (e) => {
    const val = parseFloat(e.target.value);
    setLastManualChange(Date.now());
    setLocalSpeed(val);
    if (activeEntityId !== null) setSpeed(val, activeEntityId).catch(console.error);
  };

  const isWorkshop = gameState?.state === 'WAIT_AT_WORKSHOP';

  const handleSave = async () => {
    try {
      await saveGame('game-save.json');
    } catch (err) {
      console.error("Failed to save", err);
    }
  };

  const handleLoad = async () => {
    try {
      await loadGame('game-save.json');
    } catch (err) {
      console.error("Failed to load", err);
    }
  };

  return (
    <div className="control-panel">
      <h2>Car Controls</h2>
      
      <div className="control-group">
        <label>Current Power:</label>
        <div className="power-display">
          <span className="power-icon">⚡</span>
          <span className="power-value">{gameState?.power?.toFixed(1) || 0}%</span>
        </div>
      </div>

      <div className="control-group">
        <label>Set Speed: <span className="speed-value">{localSpeed}</span></label>
        <input 
          type="range" 
          min="0" 
          max="200" 
          step="1"
          value={localSpeed}
          onChange={handleSpeedChange}
          className="speed-slider"
        />
      </div>

      <div className="control-group">
        <label>Engine Type:</label>
        <select 
          value={localEngine} 
          onChange={handleEngineChange}
          disabled={!isWorkshop}
        >
          <option value="ELECTRIC">Electric</option>
          <option value="FUEL">Fuel</option>
        </select>
      </div>

      <div className="control-group">
        <label>Car Color:</label>
        <div className="color-picker-wrapper">
          <input 
            type="color" 
            value={localColor} 
            onChange={handleColorChange} 
            disabled={!isWorkshop}
          />
          <span className="color-hex">{localColor}</span>
        </div>
      </div>

      <div className="control-group save-load-buttons">
        <button className="action-btn" onClick={handleSave}>
          💾 Save
        </button>
        <button className="action-btn" onClick={handleLoad}>
          📂 Load
        </button>
      </div>
      
      {/* Warning Box (Yellow) */}
      {gameState?.warning && (
        <div className="warning-box">
          <small>⚠️ {gameState.warning}</small>
        </div>
      )}
      
      {/* Alert Box (Red) */}
      {gameState?.message && (
        <div className="alert-box">
          <small>🚨 {gameState.message}</small>
        </div>
      )}
    </div>
  );
};

export default ControlPanel;
