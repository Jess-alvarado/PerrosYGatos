import { UserCircle } from 'lucide-react';

// 1. Recibimos onComplete como prop
const OwnerProfileForm = ({ onComplete }) => {

  const handleSubmit = (e) => {
    e.preventDefault();
    // Aquí iría tu lógica de fetch para pyg-owner
    console.log("Datos enviados a pyg-owner");

    // 2. Avisamos que terminamos
    if (onComplete) onComplete();
  };

  return (
    <div className="max-w-md mx-auto bg-[var(--bg-surface)] p-8 rounded-card border border-[var(--border-color)] shadow-[var(--card-shadow)]">
      <h2 className="text-2xl font-bold text-[var(--text-main)] mb-6 text-center">Completa tu perfil de Dueño</h2>
      
      {/* 3. Conectamos el handleSubmit */}
      <form className="space-y-5" onSubmit={handleSubmit}>
        <div>
          <label className="block text-sm font-semibold mb-2 ml-1">Nombre Completo</label>
          <div className="relative">
            <UserCircle className="absolute left-4 top-3.5 w-5 h-5 text-[var(--accent)]" />
            <input required type="text" placeholder="Juan Pérez" className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-2xl py-3.5 pl-12 pr-4 text-[var(--text-main)] outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]" />
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <input required type="text" placeholder="Región" className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-2xl py-3.5 px-4 text-[var(--text-main)] outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]" />
          <input required type="text" placeholder="Comuna" className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-2xl py-3.5 px-4 text-[var(--text-main)] outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]" />
        </div>

        <button type="submit" className="w-full bg-[var(--color-terracotta)] text-white font-bold py-4 rounded-2xl shadow-lg hover:opacity-90 transition-all mt-4 transform active:scale-95">
          Guardar en Perros&Gatos
        </button>
      </form>
    </div>
  );
};

export default OwnerProfileForm;