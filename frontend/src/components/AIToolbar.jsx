import React, { useState } from 'react';
import aiService from '../services/aiService';

const AIToolbar = ({ content, onContentUpdated, tone = 'neutral', platform = 'general' }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showQuickPrompts, setShowQuickPrompts] = useState(false);

  const quickPrompts = [
    { label: 'Make it more engaging', prompt: 'Make this content more engaging and attention-grabbing' },
    { label: 'Add emojis', prompt: 'Add relevant emojis to this content to make it more visually appealing' },
    { label: 'Shorten it', prompt: 'Make this content more concise while keeping the key message' },
    { label: 'Make it professional', prompt: 'Rewrite this content in a more professional tone' },
    { label: 'Add call-to-action', prompt: 'Add an effective call-to-action to this content' },
    { label: 'Make it casual', prompt: 'Rewrite this content in a more casual, friendly tone' }
  ];

  const handleQuickAction = async (actionType, customPrompt = '') => {
    if (!content || !content.trim()) {
      setError('No content available for improvement');
      return;
    }

    setLoading(true);
    setError('');

    try {
      let response;
      const request = {
        prompt: customPrompt || actionType,
        tone,
        platform,
        existingContent: content
      };

      switch (actionType) {
        case 'improve':
          response = await aiService.improveContent(request);
          if (response.success && onContentUpdated) {
            onContentUpdated(response.generatedContent);
          }
          break;
        case 'hashtags':
          response = await aiService.generateHashtags(request);
          if (response.success && response.hashtags && onContentUpdated) {
            const hashtagText = '\n\n' + response.hashtags.join(' ');
            onContentUpdated(content + hashtagText);
          }
          break;
        case 'variations':
          response = await aiService.generateVariations(request);
          break;
        default:
          // Custom prompt improvement
          response = await aiService.improveContent(request);
          if (response.success && onContentUpdated) {
            onContentUpdated(response.generatedContent);
          }
          break;
      }

      if (!response.success) {
        setError(response.errorMessage || 'Action failed');
      }
    } catch (error) {
      setError('Error: ' + (error.errorMessage || error));
    } finally {
      setLoading(false);
    }
  };

  const ToolbarButton = ({ onClick, children, color = '#6c757d', disabled = false }) => (
    <button
      onClick={onClick}
      disabled={disabled || loading}
      style={{
        padding: '6px 12px',
        fontSize: '0.85em',
        backgroundColor: disabled || loading ? '#f8f9fa' : color,
        color: disabled || loading ? '#6c757d' : 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: disabled || loading ? 'not-allowed' : 'pointer',
        transition: 'all 0.2s',
        ':hover': {
          opacity: 0.8
        }
      }}
    >
      {loading ? '⏳' : children}
    </button>
  );

  return (
    <div style={{
      padding: '10px',
      backgroundColor: '#f8f9fa',
      borderRadius: '6px',
      marginBottom: '10px',
      border: '1px solid #e9ecef'
    }}>
      <div style={{
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
        flexWrap: 'wrap',
        marginBottom: error ? '8px' : '0'
      }}>
        <span style={{ fontSize: '0.9em', fontWeight: '500', color: '#495057' }}>
          🤖 AI Tools:
        </span>

        <ToolbarButton
          onClick={() => handleQuickAction('improve')}
          color="#28a745"
        >
          ✨ Improve
        </ToolbarButton>

        <ToolbarButton
          onClick={() => handleQuickAction('hashtags')}
          color="#17a2b8"
        >
          # Hashtags
        </ToolbarButton>

        <ToolbarButton
          onClick={() => setShowQuickPrompts(!showQuickPrompts)}
          color="#6f42c1"
        >
          ⚡ Quick Actions
        </ToolbarButton>

        <div style={{
          borderLeft: '1px solid #dee2e6',
          paddingLeft: '8px',
          marginLeft: '4px'
        }}>
          <span style={{ fontSize: '0.8em', color: '#6c757d' }}>
            Tone: {tone} | Platform: {platform}
          </span>
        </div>
      </div>

      {/* Quick Prompts Dropdown */}
      {showQuickPrompts && (
        <div style={{
          marginTop: '8px',
          padding: '10px',
          backgroundColor: 'white',
          border: '1px solid #dee2e6',
          borderRadius: '4px',
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
        }}>
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))',
            gap: '6px'
          }}>
            {quickPrompts.map((prompt, index) => (
              <button
                key={index}
                onClick={() => handleQuickAction('improve', prompt.prompt)}
                disabled={loading}
                style={{
                  padding: '6px 10px',
                  fontSize: '0.8em',
                  backgroundColor: loading ? '#f8f9fa' : '#e9ecef',
                  color: loading ? '#6c757d' : '#495057',
                  border: '1px solid #ced4da',
                  borderRadius: '3px',
                  cursor: loading ? 'not-allowed' : 'pointer',
                  textAlign: 'left',
                  transition: 'all 0.2s'
                }}
                onMouseEnter={(e) => {
                  if (!loading) {
                    e.target.style.backgroundColor = '#dee2e6';
                  }
                }}
                onMouseLeave={(e) => {
                  if (!loading) {
                    e.target.style.backgroundColor = '#e9ecef';
                  }
                }}
              >
                {prompt.label}
              </button>
            ))}
          </div>
          <button
            onClick={() => setShowQuickPrompts(false)}
            style={{
              marginTop: '8px',
              padding: '4px 8px',
              fontSize: '0.75em',
              backgroundColor: 'transparent',
              color: '#6c757d',
              border: 'none',
              cursor: 'pointer',
              textDecoration: 'underline'
            }}
          >
            Hide Quick Actions
          </button>
        </div>
      )}

      {/* Error Display */}
      {error && (
        <div style={{
          color: '#dc3545',
          fontSize: '0.85em',
          marginTop: '8px',
          padding: '6px 8px',
          backgroundColor: '#f8d7da',
          border: '1px solid #f5c6cb',
          borderRadius: '3px'
        }}>
          {error}
        </div>
      )}

      {loading && (
        <div style={{
          fontSize: '0.85em',
          color: '#6c757d',
          marginTop: '8px',
          fontStyle: 'italic'
        }}>
          AI is working on your content...
        </div>
      )}
    </div>
  );
};

export default AIToolbar;