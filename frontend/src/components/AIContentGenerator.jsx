import React, { useState } from 'react';
import aiService from '../services/aiService';

const AIContentGenerator = ({ onContentGenerated, existingContent = '' }) => {
  const [prompt, setPrompt] = useState('');
  const [tone, setTone] = useState('neutral');
  const [platform, setPlatform] = useState('general');
  const [contentType, setContentType] = useState('post');
  const [loading, setLoading] = useState(false);
  const [generatedContent, setGeneratedContent] = useState('');
  const [hashtags, setHashtags] = useState([]);
  const [variations, setVariations] = useState([]);
  const [error, setError] = useState('');

  const handleGenerateContent = async () => {
    if (!prompt.trim()) {
      setError('Please enter a prompt');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const request = {
        prompt,
        tone,
        platform,
        contentType,
        existingContent
      };

      const response = await aiService.generateContent(request);

      if (response.success) {
        setGeneratedContent(response.generatedContent);
        if (onContentGenerated) {
          onContentGenerated(response.generatedContent);
        }
      } else {
        setError(response.errorMessage || 'Failed to generate content');
      }
    } catch (error) {
      setError('Error generating content: ' + (error.errorMessage || error));
    } finally {
      setLoading(false);
    }
  };

  const handleImproveContent = async () => {
    if (!existingContent.trim()) {
      setError('No content to improve');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const request = {
        prompt: prompt || 'Improve this content',
        tone,
        platform,
        contentType,
        existingContent
      };

      const response = await aiService.improveContent(request);

      if (response.success) {
        setGeneratedContent(response.generatedContent);
        if (onContentGenerated) {
          onContentGenerated(response.generatedContent);
        }
      } else {
        setError(response.errorMessage || 'Failed to improve content');
      }
    } catch (error) {
      setError('Error improving content: ' + (error.errorMessage || error));
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateHashtags = async () => {
    const contentToAnalyze = existingContent || generatedContent;
    if (!contentToAnalyze.trim() && !prompt.trim()) {
      setError('Please provide content or a prompt for hashtag generation');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const request = {
        prompt: prompt || contentToAnalyze,
        platform,
        existingContent: contentToAnalyze
      };

      const response = await aiService.generateHashtags(request);

      if (response.success) {
        setHashtags(response.hashtags || []);
      } else {
        setError(response.errorMessage || 'Failed to generate hashtags');
      }
    } catch (error) {
      setError('Error generating hashtags: ' + (error.errorMessage || error));
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateVariations = async () => {
    const contentToVary = existingContent || generatedContent;
    if (!contentToVary.trim() && !prompt.trim()) {
      setError('Please provide content or a prompt for variations');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const request = {
        prompt,
        tone,
        platform,
        contentType,
        existingContent: contentToVary
      };

      const response = await aiService.generateVariations(request);

      if (response.success) {
        setVariations(response.variations || []);
      } else {
        setError(response.errorMessage || 'Failed to generate variations');
      }
    } catch (error) {
      setError('Error generating variations: ' + (error.errorMessage || error));
    } finally {
      setLoading(false);
    }
  };

  const useVariation = (variation) => {
    setGeneratedContent(variation);
    if (onContentGenerated) {
      onContentGenerated(variation);
    }
  };

  return (
    <div className="ai-content-generator" style={{ padding: '20px', border: '1px solid #ddd', borderRadius: '8px', marginBottom: '20px' }}>
      <h3 style={{ marginBottom: '15px', color: '#333' }}>🤖 AI Content Generator</h3>

      {/* Input Section */}
      <div style={{ marginBottom: '15px' }}>
        <textarea
          value={prompt}
          onChange={(e) => setPrompt(e.target.value)}
          placeholder="Enter your prompt or topic (e.g., 'Write about sustainable living tips')"
          style={{
            width: '100%',
            height: '80px',
            padding: '10px',
            border: '1px solid #ccc',
            borderRadius: '4px',
            resize: 'vertical'
          }}
        />
      </div>

      {/* Controls Section */}
      <div style={{ display: 'flex', gap: '10px', marginBottom: '15px', flexWrap: 'wrap' }}>
        <select
          value={tone}
          onChange={(e) => setTone(e.target.value)}
          style={{ padding: '5px', border: '1px solid #ccc', borderRadius: '4px' }}
        >
          <option value="neutral">Neutral</option>
          <option value="professional">Professional</option>
          <option value="casual">Casual</option>
          <option value="humorous">Humorous</option>
          <option value="inspirational">Inspirational</option>
          <option value="educational">Educational</option>
        </select>

        <select
          value={platform}
          onChange={(e) => setPlatform(e.target.value)}
          style={{ padding: '5px', border: '1px solid #ccc', borderRadius: '4px' }}
        >
          <option value="general">General</option>
          <option value="twitter">Twitter</option>
          <option value="facebook">Facebook</option>
          <option value="instagram">Instagram</option>
          <option value="linkedin">LinkedIn</option>
          <option value="tiktok">TikTok</option>
        </select>

        <select
          value={contentType}
          onChange={(e) => setContentType(e.target.value)}
          style={{ padding: '5px', border: '1px solid #ccc', borderRadius: '4px' }}
        >
          <option value="post">Post</option>
          <option value="caption">Caption</option>
          <option value="story">Story</option>
          <option value="thread">Thread</option>
        </select>
      </div>

      {/* Action Buttons */}
      <div style={{ display: 'flex', gap: '10px', marginBottom: '15px', flexWrap: 'wrap' }}>
        <button
          onClick={handleGenerateContent}
          disabled={loading}
          style={{
            padding: '8px 16px',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer'
          }}
        >
          {loading ? 'Generating...' : 'Generate Content'}
        </button>

        {existingContent && (
          <button
            onClick={handleImproveContent}
            disabled={loading}
            style={{
              padding: '8px 16px',
              backgroundColor: '#28a745',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: loading ? 'not-allowed' : 'pointer'
            }}
          >
            {loading ? 'Improving...' : 'Improve Content'}
          </button>
        )}

        <button
          onClick={handleGenerateHashtags}
          disabled={loading}
          style={{
            padding: '8px 16px',
            backgroundColor: '#17a2b8',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer'
          }}
        >
          {loading ? 'Generating...' : 'Generate Hashtags'}
        </button>

        <button
          onClick={handleGenerateVariations}
          disabled={loading}
          style={{
            padding: '8px 16px',
            backgroundColor: '#6f42c1',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer'
          }}
        >
          {loading ? 'Generating...' : 'Generate Variations'}
        </button>
      </div>

      {/* Error Display */}
      {error && (
        <div style={{ color: 'red', marginBottom: '15px', padding: '10px', backgroundColor: '#ffebee', border: '1px solid #ffcdd2', borderRadius: '4px' }}>
          {error}
        </div>
      )}

      {/* Generated Content */}
      {generatedContent && (
        <div style={{ marginBottom: '15px' }}>
          <h4>Generated Content:</h4>
          <div style={{
            padding: '10px',
            backgroundColor: '#f8f9fa',
            border: '1px solid #e9ecef',
            borderRadius: '4px',
            whiteSpace: 'pre-wrap'
          }}>
            {generatedContent}
          </div>
        </div>
      )}

      {/* Hashtags */}
      {hashtags.length > 0 && (
        <div style={{ marginBottom: '15px' }}>
          <h4>Generated Hashtags:</h4>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '5px' }}>
            {hashtags.map((hashtag, index) => (
              <span
                key={index}
                style={{
                  padding: '3px 8px',
                  backgroundColor: '#e3f2fd',
                  border: '1px solid #bbdefb',
                  borderRadius: '12px',
                  fontSize: '0.9em'
                }}
              >
                {hashtag}
              </span>
            ))}
          </div>
        </div>
      )}

      {/* Variations */}
      {variations.length > 0 && (
        <div style={{ marginBottom: '15px' }}>
          <h4>Content Variations:</h4>
          {variations.map((variation, index) => (
            <div
              key={index}
              style={{
                padding: '10px',
                margin: '5px 0',
                backgroundColor: '#f1f3f4',
                border: '1px solid #dadce0',
                borderRadius: '4px',
                position: 'relative'
              }}
            >
              <div style={{ whiteSpace: 'pre-wrap', marginBottom: '10px' }}>
                {variation}
              </div>
              <button
                onClick={() => useVariation(variation)}
                style={{
                  padding: '4px 8px',
                  fontSize: '0.8em',
                  backgroundColor: '#007bff',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer'
                }}
              >
                Use This
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default AIContentGenerator;