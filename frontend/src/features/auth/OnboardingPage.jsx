import OwnerProfileForm from '../owners/OwnerProfileForm';
import ProfessionalProfileForm from '../professionals/ProfessionalProfileForm';

// 1. Agregamos onComplete a las props que recibe el componente
const OnboardingPage = ({ userRole, onComplete }) => {
  
  return (
    <div className="min-h-screen bg-[var(--bg-main)] py-12 px-4 transition-colors duration-300">
      <div className="max-w-4xl mx-auto">

        {/* Encabezado de bienvenida general */}
        <div className="text-center mb-10">
          <h1 className="text-4xl font-black text-[var(--text-main)] mb-3">
            ¡Hola! <span className="text-[var(--color-terracotta)]">Ya casi terminamos</span>
          </h1>
          <p className="text-[var(--text-muted)]">
            Necesitamos unos últimos datos para activar tu cuenta en la plataforma.
          </p>
        </div>

        {/* Lógica de Tráfico: Pasamos la prop onComplete a los hijos */}
        <div className="animate-in fade-in slide-in-from-bottom-4 duration-700">
          {userRole === 'ROLE_OWNER' ? (
            // 2. IMPORTANTE: Pasamos la función aquí
            <OwnerProfileForm onComplete={onComplete} />
          ) : (
            // 2. Y aquí también
            <ProfessionalProfileForm onComplete={onComplete} />
          )}
        </div>

        {/* Pie de página simple */}
        <p className="text-center text-xs text-[var(--text-muted)] mt-12 opacity-50">
          Tus datos serán procesados por los servicios de {userRole === 'ROLE_OWNER' ? 'pyg-owner' : 'pyg-professional'}.
        </p>
      </div>
    </div>
  );
};

export default OnboardingPage;