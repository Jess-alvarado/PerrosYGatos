import {
  BrowserRouter as Router,
  Routes,
  Route,
  Link,
  Navigate,
} from "react-router-dom";
import { useState } from "react";
import { ShieldCheck, Sparkles, ArrowRight } from "lucide-react";

import Navbar from "./components/layout/Navbar";
import LoginPage from "./features/auth/LoginPage";
import RegisterPage from "./features/auth/RegisterPage";
import OnboardingPage from "./features/auth/OnboardingPage";
import ProfessionalProfilePage from "./features/professionals/ProfessionalProfilePage";
import OwnerProfilePage from "./features/owners/OwnerProfilePage";
import CommunityPage from "./features/community/CommunityPage";
import CreatePetPage from "./features/owners/CreatePetPage";

const Home = () => {
  return (
    <div className="pt-8 pb-6">
      <div className="max-w-6xl mx-auto px-6 text-center mb-16 animate-in fade-in slide-in-from-top-8 duration-1000">
        <span className="inline-block px-4 py-1.5 mb-6 text-sm font-bold tracking-wider text-[var(--color-forest)] bg-[var(--accent)]/20 rounded-full uppercase">
          Bienvenido a Perros & Gatos
        </span>
        <h2 className="text-5xl md:text-7xl font-black mb-6 text-[var(--text-main)] leading-tight">
          Cuidamos a los que <br />
          <span className="text-[var(--color-terracotta)]">más quieres</span>
        </h2>
        <p className="max-w-2xl mx-auto text-lg text-[var(--text-muted)] mb-9 leading-relaxed">
          La plataforma integral donde dueños y profesionales se encuentran.
        </p>

        <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
          <Link
            to="/register"
            className="btn-primary w-full sm:w-auto flex items-center justify-center gap-2"
          >
            Comenzar ahora <ArrowRight size={20} />
          </Link>
          <Link
            to="/login"
            className="px-8 py-4 font-bold text-[var(--text-main)] hover:text-[var(--color-terracotta)] transition-colors"
          >
            Ya tengo cuenta
          </Link>
        </div>
      </div>

      <div className="max-w-6xl mx-auto px-6 grid grid-cols-1 md:grid-cols-2 gap-8">
        <div className="bg-[var(--bg-surface)] p-8 rounded-card border border-[var(--border-color)]">
          <div className="w-12 h-12 bg-blue-100 rounded-2xl flex items-center justify-center text-blue-500 mb-6">
            <ShieldCheck fill="currentColor" />
          </div>
          <h4 className="text-xl font-bold mb-3 text-[var(--text-main)]">
            Profesionales
          </h4>
          <p className="text-[var(--text-muted)] text-sm leading-relaxed">
            Etólogos y entrenadores verificados.
          </p>
        </div>
        <div className="bg-[var(--bg-surface)] p-8 rounded-card border border-[var(--border-color)]">
          <div className="w-12 h-12 bg-amber-100 rounded-2xl flex items-center justify-center text-amber-500 mb-6">
            <Sparkles fill="currentColor" />
          </div>
          <h4 className="text-xl font-bold mb-3 text-[var(--text-main)]">
            Comunidad
          </h4>
          <p className="text-[var(--text-muted)] text-sm leading-relaxed">
            Comparte experiencias con otros dueños.
          </p>
        </div>
      </div>
    </div>
  );
};

function App() {
  const [userRole, setUserRole] = useState(
    localStorage.getItem("role") || null,
  );

  const handleAuthSuccess = (role) => {
    setUserRole(role);
    localStorage.setItem("role", role);
  };

  return (
    <Router>
      <div className="min-h-screen bg-[var(--bg-main)] transition-colors duration-300">
        <Navbar userRole={userRole} />

        <main>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route
              path="/login"
              element={<LoginPage onFinishAuth={handleAuthSuccess} />}
            />
            <Route
              path="/register"
              element={<RegisterPage onFinishAuth={handleAuthSuccess} />}
            />

            <Route path="/community" element={<CommunityPage />} />

            <Route
              path="/onboarding"
              element={<OnboardingPage userRole={userRole} />}
            />

            <Route
              path="/profile"
              element={
                userRole === "ROLE_OWNER" ? (
                  <OwnerProfilePage />
                ) : (
                  <ProfessionalProfilePage />
                )
              }
            />

            <Route path="/pets/create" element={<CreatePetPage />} />
            <Route path="*" element={<Navigate to="/" />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
