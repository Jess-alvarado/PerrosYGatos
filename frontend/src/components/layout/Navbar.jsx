import { Sun, Moon, LogIn, PawPrint, Users, HeartPulse, User } from 'lucide-react';
import { useTheme } from '../../context/ThemeContext';

const Navbar = ({ onLoginClick, onLogoClick, userRole, setView }) => {
  const { isDarkMode, toggleTheme } = useTheme();

  // Definimos los links según el rol
  const navLinks = {
    'ROLE_OWNER': [
      { label: 'Mis Mascotas', icon: <PawPrint size={18}/>, view: 'my-pets' },
      { label: 'Profesionales', icon: <Users size={18}/>, view: 'professionals' },
      { label: 'Convivencia', icon: <HeartPulse size={18}/>, view: 'community' },
      { label: 'Mi Perfil', icon: <User size={18}/>, view: 'profile' },
    ],
    'ROLE_PROFESSIONAL': [
      { label: 'Mi Perfil', icon: <User size={18}/>, view: 'profile' },
      { label: 'Convivencia', icon: <HeartPulse size={18}/>, view: 'community' },
      { label: 'Casos Clínicos', icon: <PawPrint size={18}/>, view: 'pet-directory' },
    ]
  };

  const currentLinks = navLinks[userRole] || [];

  return (
    <nav className="w-full px-8 py-5 flex justify-between items-center bg-[var(--bg-surface)] border-b border-[var(--border-color)] shadow-sm transition-all duration-300 sticky top-0 z-50">

      {/* Logo */}
      <div className="flex items-center gap-2 cursor-pointer group" onClick={onLogoClick}>
        <span className="text-2xl font-black text-[var(--accent)] tracking-tighter transition-colors">
          Perros <span className="text-[var(--color-terracotta)] group-hover:text-[#c4664a]">&</span> Gatos
        </span>
      </div>

      {/* Links Dinámicos (Solo si hay un rol activo) */}
      <div className="hidden md:flex items-center gap-6">
        {currentLinks.map((link) => (
          <button
            key={link.view}
            onClick={() => setView(link.view)}
            className="flex items-center gap-2 text-sm font-semibold text-[var(--text-muted)] hover:text-[var(--color-terracotta)] transition-colors px-2 py-1"
          >
            {link.icon}
            {link.label}
          </button>
        ))}
      </div>

      <div className="flex items-center gap-5">
        <button
          onClick={toggleTheme}
          className="p-3 rounded-xl hover:bg-black/5 dark:hover:bg-white/5 transition-colors group"
        >
          {isDarkMode ?
            <Sun size={20} className="text-[var(--color-peach)] group-hover:rotate-45 transition-transform" /> : 
            <Moon size={20} className="text-[var(--accent)] group-hover:-rotate-12 transition-transform" />
          }
        </button>

        {/* Si no hay rol, mostramos login. Si hay, podríamos mostrar un botón de Logout */}
        {!userRole ? (
          <button
            onClick={onLoginClick}
            className="flex items-center gap-2.5 bg-[var(--color-terracotta)] hover:bg-[#c4664a] text-white px-7 py-3 rounded-xl text-sm font-bold transition-all transform active:scale-95 shadow-lg shadow-terracotta/20"
          >
            <LogIn size={18} />
            Iniciar Sesión
          </button>
        ) : (
          <div className="w-10 h-10 rounded-full bg-[var(--accent)] flex items-center justify-center text-white font-bold border-2 border-[var(--border-color)]">
            {userRole === 'ROLE_OWNER' ? 'O' : 'P'}
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;