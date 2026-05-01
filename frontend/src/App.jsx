import { useState } from 'react';
import { ShieldCheck, Sparkles, ArrowRight } from 'lucide-react';
import Navbar from './components/layout/Navbar';
import LoginPage from './features/auth/LoginPage';
import RegisterPage from './features/auth/RegisterPage';
import OnboardingPage from './features/auth/OnboardingPage';
import ProfessionalProfilePage from './features/professionals/ProfessionalProfilePage';
import OwnerProfilePage from './features/owners/OwnerProfilePage';
import CommunityPage from './features/community/CommunityPage';
import PetDirectoryPage from './features/professionals/PetDirectoryPage';
import MyPetsPage from './features/owners/MyPetsPage';
import ProfessionalsSearchPage from './features/owners/ProfessionalsSearchPage';

function App() {
  const [view, setView] = useState('home');
  const [userRole, setUserRole] = useState(null);

  const handleStartOnboarding = (role) => {
    setUserRole(role);
    setView('onboarding');
  };

  return (
    <div className="min-h-screen bg-[var(--bg-main)] transition-colors duration-300">
      <Navbar
        onLoginClick={() => setView('login')}
        onLogoClick={() => setView('home')}
        userRole={userRole}
        setView={setView}
      />

      <main>
        {view === 'home' && (
          <div className="pt-8 pb-6">
            {/* Hero Section */}
            <div className="max-w-6xl mx-auto px-6 text-center mb-16 animate-in fade-in slide-in-from-top-8 duration-1000">
              <span className="inline-block px-4 py-1.5 mb-6 text-sm font-bold tracking-wider text-[var(--color-forest)] bg-[var(--accent)]/20 rounded-full uppercase">
                Bienvenido a Perros & Gatos
              </span>
              <h2 className="text-5xl md:text-7xl font-black mb-6 text-[var(--text-main)] leading-tight">
                Cuidamos a los que <br />
                <span className="text-[var(--color-terracotta)]">más quieres</span>
              </h2>
              <p className="max-w-2xl mx-auto text-lg text-[var(--text-muted)] mb-9 leading-relaxed">
                La plataforma integral donde dueños y profesionales del comportamiento animal se encuentran para mejorar la vida de nuestros peludos.
              </p>

              <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
                <button onClick={() => setView('register')} className="btn-primary w-full sm:w-auto">
                  Comenzar ahora <ArrowRight size={20} />
                </button>
                <button
                  onClick={() => setView('login')}
                  className="px-8 py-4 font-bold text-[var(--text-main)] hover:text-[var(--color-terracotta)] transition-colors"
                >
                  Ya tengo cuenta
                </button>
              </div>
            </div>

            {/* Feature Cards */}
            <div className="max-w-6xl mx-auto px-6 grid grid-cols-1 md:grid-cols-2 gap-8">
              <div className="bg-[var(--bg-surface)] p-8 rounded-card border border-[var(--border-color)] shadow-sm hover:shadow-xl transition-all">
                <div className="w-12 h-12 bg-blue-100 rounded-2xl flex items-center justify-center text-blue-500 mb-6">
                  <ShieldCheck fill="currentColor" />
                </div>
                <h4 className="text-xl font-bold mb-3 text-[var(--text-main)]">Profesionales</h4>
                <p className="text-[var(--text-muted)] text-sm leading-relaxed">
                  Conecta con etólogos y entrenadores verificados para asesorías especializadas.
                </p>
              </div>

              <div className="bg-[var(--bg-surface)] p-8 rounded-card border border-[var(--border-color)] shadow-sm hover:shadow-xl transition-all">
                <div className="w-12 h-12 bg-amber-100 rounded-2xl flex items-center justify-center text-amber-500 mb-6">
                  <Sparkles fill="currentColor" />
                </div>
                <h4 className="text-xl font-bold mb-3 text-[var(--text-main)]">Comunidad</h4>
                <p className="text-[var(--text-muted)] text-sm leading-relaxed">
                  Comparte experiencias y resuelve dudas de convivencia con profesionales.
                </p>
              </div>
            </div>
          </div>
        )}

        {view === 'login' && <LoginPage />}

        {view === 'register' && (
          <RegisterPage onFinishAuth={handleStartOnboarding} />
        )}

        {view === 'onboarding' && (
          <OnboardingPage
            userRole={userRole}
            onComplete={() => setView('profile')}
          />
        )}

        {view === 'profile' && (
          userRole === 'ROLE_OWNER' ? <OwnerProfilePage /> : <ProfessionalProfilePage />
        )}

        {view === 'community' && <CommunityPage />}

        {view === 'pet-directory' && <PetDirectoryPage />}

        {view === 'my-pets' && <MyPetsPage />}

        {view === 'professionals' && <ProfessionalsSearchPage />}
      </main>
    </div>
  );
}

export default App;