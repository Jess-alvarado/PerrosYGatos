import { useState } from 'react';
import { Mail, Lock, Eye, EyeOff, PawPrint } from 'lucide-react';

const LoginPage = () => {
  const [showPassword, setShowPassword] = useState(false);

  return (
    <div className="min-h-screen flex items-center justify-center bg-[var(--bg-main)] p-4">
      {/* Tarjeta principal con bordes extra redondeados como en tus fotos */}
      <div className="w-full max-w-md bg-[var(--bg-surface)] rounded-[2rem] shadow-xl p-8 border border-[var(--border-color)] transition-colors duration-300">

        {/* Logo / Encabezado */}
        <div className="flex flex-col items-center mb-8">
          <div className="bg-[var(--color-terracotta)] p-3 rounded-2xl mb-4 shadow-lg shadow-terracotta/20">
            <PawPrint className="text-white w-8 h-8" />
          </div>
          <h1 className="text-2xl font-bold text-[var(--text-main)]">Bienvenido de nuevo</h1>
          <p className="text-[var(--text-muted)] text-sm mt-2">Ingresa a Perros&Gatos</p>
        </div>

        <form className="space-y-6">
          {/* Email */}
          <div>
            <label className="block text-sm font-medium text-[var(--text-main)] mb-2 ml-1">
              Correo Electrónico
            </label>
            <div className="relative">
              <Mail className="absolute left-4 top-3.5 w-5 h-5 text-[var(--color-sage)]" />
              <input
                type="email"
                placeholder="tu@email.com"
                className="w-full bg-[var(--bg-main)] text-[var(--text-main)] border border-[var(--border-color)] rounded-xl py-3 pl-12 pr-4 focus:ring-2 focus:ring-[var(--color-terracotta)] focus:outline-none transition-all"
              />
            </div>
          </div>

          {/* Password */}
          <div>
            <label className="block text-sm font-medium text-[var(--text-main)] mb-2 ml-1">
              Contraseña
            </label>
            <div className="relative">
              <Lock className="absolute left-4 top-3.5 w-5 h-5 text-[var(--color-sage)]" />
              <input
                type={showPassword ? "text" : "password"}
                placeholder="••••••••"
                className="w-full bg-[var(--bg-main)] text-[var(--text-main)] border border-[var(--border-color)] rounded-xl py-3 pl-12 pr-12 focus:ring-2 focus:ring-[var(--color-terracotta)] focus:outline-none transition-all"
              />
                <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-4 top-4 text-[var(--text-muted)] hover:text-terracotta"
              >
                    {/* CORRECCIÓN AQUÍ: className dentro de los componentes de icono */}
                    {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
            </div>
          </div>

          {/* Recordar y Olvido */}
          <div className="flex items-center justify-between text-xs">
            <label className="flex items-center text-[var(--text-muted)] cursor-pointer">
              <input type="checkbox" className="mr-2 rounded border-[var(--border-color)] accent-[var(--color-terracotta)]" />
              Recordarme
            </label>
            <a href="#" className="text-[var(--color-forest)] font-semibold hover:underline">
              ¿Olvidaste tu contraseña?
            </a>
          </div>

          {/* Botón Terracotta (Siguiendo 4tCXMLD.JPG) */}
          <button
            type="submit"
            className="w-full bg-[var(--color-terracotta)] hover:bg-[#c4664a] text-white font-bold py-4 rounded-2xl shadow-lg transition-all transform hover:-translate-y-1 active:scale-95"
          >
            Iniciar Sesión
          </button>
        </form>

        {/* Registro */}
        <p className="text-center text-[var(--text-muted)] text-sm mt-8">
          ¿No tienes cuenta? {' '}
          <a href="#" className="text-[var(--color-terracotta)] font-bold hover:underline">
            Regístrate aquí
          </a>
        </p>
      </div>
    </div>
  );
};

export default LoginPage;