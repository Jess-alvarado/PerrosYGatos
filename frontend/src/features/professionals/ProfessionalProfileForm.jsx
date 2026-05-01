import { Briefcase, Award, DollarSign } from 'lucide-react';

// 1. Recibimos onComplete como prop
const ProfessionalProfileForm = ({ onComplete }) => {
  
  const handleSubmit = (e) => {
    e.preventDefault();
    // Aquí iría tu lógica de fetch para pyg-professional
    console.log("Datos enviados a pyg-professional");
    
    // 2. Ejecutamos el aviso para cambiar la vista
    if (onComplete) onComplete();
  };

  return (
    <div className="max-w-lg mx-auto bg-[var(--bg-surface)] p-8 rounded-card border border-[var(--border-color)] shadow-[var(--card-shadow)]">
      <h2 className="text-2xl font-bold text-[var(--text-main)] mb-2 text-center">Registro Profesional</h2>
      <p className="text-[var(--text-muted)] text-sm text-center mb-8">Información para pyg-professional</p>

      {/* 3. Conectamos el handleSubmit */}
      <form className="space-y-5" onSubmit={handleSubmit}>
        <div>
          <label className="block text-sm font-semibold mb-2">Especialidad o Título</label>
          <div className="relative">
            <Briefcase className="absolute left-4 top-3.5 w-5 h-5 text-[var(--accent)]" />
            <input required type="text" placeholder="Ej: Etólogo Clínico" className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-2xl py-3.5 pl-12 pr-4 text-[var(--text-main)] outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]" />
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div className="relative">
            <Award className="absolute left-4 top-3.5 w-5 h-5 text-[var(--accent)]" />
            <input required type="number" placeholder="Años Exp." className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-2xl py-3.5 pl-12 pr-4 text-[var(--text-main)] outline-none" />
          </div>
          <div className="relative">
            <DollarSign className="absolute left-4 top-3.5 w-5 h-5 text-[var(--accent)]" />
            <input required type="text" placeholder="Precio" className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-2xl py-3.5 pl-12 pr-4 text-[var(--text-main)] outline-none" />
          </div>
        </div>

        <textarea required placeholder="Cuéntanos sobre tu formación..." className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-2xl py-4 px-4 text-[var(--text-main)] outline-none h-32 resize-none focus:ring-2 focus:ring-[var(--color-terracotta)]"></textarea>

        <button type="submit" className="w-full bg-[var(--color-terracotta)] text-white font-bold py-4 rounded-2xl shadow-lg hover:opacity-90 transition-all transform active:scale-95">
          Activar Perfil Profesional
        </button>
      </form>
    </div>
  );
};

export default ProfessionalProfileForm;