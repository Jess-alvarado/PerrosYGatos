import { useState } from 'react';
import { Mail, Lock, UserCircle, PawPrint } from 'lucide-react';

const RegisterPage = ({ onFinishAuth }) => {
  // Estado para manejar el rol seleccionado
  const [role, setRole] = useState('owner');

  // Función que maneja el envío del formulario
  const handleSubmit = (e) => {
    e.preventDefault();
    
    // Convertimos el rol al formato de tus microservicios
    const finalRole = role === 'owner' ? 'ROLE_OWNER' : 'ROLE_PROFESSIONAL';
    
    // Ejecutamos la función que viene de App.jsx para cambiar la vista
    if (onFinishAuth) {
      onFinishAuth(finalRole);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-[var(--bg-main)] p-4 transition-colors duration-300">
      <div className="w-full max-w-md bg-[var(--bg-surface)] rounded-card shadow-[var(--card-shadow)] p-10 border border-[var(--border-color)]">

        {/* Logo / Encabezado */}
        <div className="flex flex-col items-center mb-8">
          <div className="bg-[var(--color-terracotta)] p-3 rounded-2xl mb-4 shadow-lg shadow-terracotta/20">
            <PawPrint className="text-white w-7 h-7" />
          </div>
          <h1 className="text-2xl font-bold text-[var(--text-main)]">Crear Cuenta</h1>
          <p className="text-[var(--text-muted)] text-sm mt-1">Únete a la comunidad de Perros&Gatos</p>
        </div>

        {/* SELECTOR DE ROL */}
        <div className="mb-8">
          <label className="block text-sm font-bold text-[var(--text-main)] mb-3 text-center">
            ¿Cómo quieres unirte?
          </label>
          <div className="grid grid-cols-2 gap-3 p-1 bg-[var(--bg-main)] rounded-2xl border border-[var(--border-color)]">
            <button
              type="button"
              onClick={() => setRole('owner')}
              className={`py-3 rounded-xl text-sm font-bold transition-all ${
                role === 'owner'
                ? 'bg-[var(--accent)] text-white shadow-md'
                : 'text-[var(--text-muted)] hover:text-[var(--text-main)]'
              }`}
            >
              Soy Dueño
            </button>
            <button
              type="button"
              onClick={() => setRole('professional')}
              className={`py-3 rounded-xl text-sm font-bold transition-all ${
                role === 'professional'
                ? 'bg-[var(--accent)] text-white shadow-md'
                : 'text-[var(--text-muted)] hover:text-[var(--text-main)]'
              }`}
            >
              Soy Profesional
            </button>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">
          {/* Nombre de Usuario */}
          <div>
            <label className="block text-sm font-semibold text-[var(--text-main)] mb-2 ml-1">
              Nombre de Usuario
            </label>
            <div className="relative">
              <UserCircle className="absolute left-4 top-3.5 w-5 h-5 text-[var(--accent)]" />
              <input
                type="text"
                required
                placeholder="ej: usuario123"
                className="w-full bg-[var(--bg-main)] text-[var(--text-main)] border border-[var(--border-color)] rounded-2xl py-3.5 pl-12 pr-4 focus:ring-2 focus:ring-[var(--color-terracotta)] focus:outline-none transition-all"
              />
            </div>
          </div>

          {/* Email */}
          <div>
            <label className="block text-sm font-semibold text-[var(--text-main)] mb-2 ml-1">
              Correo Electrónico
            </label>
            <div className="relative">
              <Mail className="absolute left-4 top-3.5 w-5 h-5 text-[var(--accent)]" />
              <input
                type="email"
                required
                placeholder="tu@email.com"
                className="w-full bg-[var(--bg-main)] text-[var(--text-main)] border border-[var(--border-color)] rounded-2xl py-3.5 pl-12 pr-4 focus:ring-2 focus:ring-[var(--color-terracotta)] focus:outline-none transition-all"
              />
            </div>
          </div>

          {/* Password */}
          <div>
            <label className="block text-sm font-semibold text-[var(--text-main)] mb-2 ml-1">
              Contraseña
            </label>
            <div className="relative">
              <Lock className="absolute left-4 top-3.5 w-5 h-5 text-[var(--accent)]" />
              <input
                type="password"
                required
                placeholder="••••••••"
                className="w-full bg-[var(--bg-main)] text-[var(--text-main)] border border-[var(--border-color)] rounded-2xl py-3.5 pl-12 pr-4 focus:ring-2 focus:ring-[var(--color-terracotta)] focus:outline-none transition-all"
              />
            </div>
          </div>

          {/* Botón Principal (Terracotta) */}
          <button
            type="submit"
            className="w-full bg-[var(--color-terracotta)] hover:bg-[#c4664a] text-white font-bold py-4 rounded-2xl shadow-lg shadow-terracotta/20 transition-all transform hover:-translate-y-1 active:scale-95 mt-4"
          >
            Siguiente paso
          </button>
        </form>

        <p className="text-center text-[var(--text-muted)] text-sm mt-8">
          ¿Ya tienes cuenta? {' '}
          <a href="#" className="text-[var(--color-terracotta)] font-bold hover:underline">
            Inicia sesión
          </a>
        </p>
      </div>
    </div>
  );
};

export default RegisterPage;