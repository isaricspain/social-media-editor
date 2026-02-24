import React, { useEffect, useState } from 'react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import LoginScreen from './components/LoginScreen';
import Dashboard from './components/Dashboard';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [user, setUser] = useState(null);

  const handleLoginSuccess = (userData) => {
    setIsLoggedIn(true);
    setUser(userData);
  };

  const handleLogout = () => {
    setIsLoggedIn(false);
    setUser(null);
    localStorage.removeItem('token');
  };

  // On initial load, set logged-in state based on presence of JWT in localStorage
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      setIsLoggedIn(true);
      // Optionally, we could decode or fetch user info here; for now keep existing null user until login flow provides it
    }
  }, []);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      {!isLoggedIn ? (
        <LoginScreen onLoginSuccess={handleLoginSuccess} />
      ) : (
        <Dashboard user={user} onLogout={handleLogout} />
      )}
    </ThemeProvider>
  );
}

export default App;