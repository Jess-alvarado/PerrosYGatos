import { Plus, Heart, Calendar, Activity } from 'lucide-react';

const MyPetsPage = () => {
  const pets = [
    { id: 1, name: "Luna", breed: "Siamés", status: "Al día", lastCheck: "12 Mar" },
    { id: 2, name: "Thor", breed: "Labrador", status: "Vacuna pendiente", lastCheck: "05 Ene" }
  ];

  return (
    <div className="max-w-6xl mx-auto p-8 animate-in fade-in duration-500">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h3 className="text-3xl font-black text-[var(--text-main)]">
            Mis <span className="text-[var(--color-terracotta)]">Mascotas</span>
          </h3>
          <p className="text-[var(--text-muted)]">Gestiona la salud y bienestar de tus compañeros</p>
        </div>
        <button className="bg-[var(--accent)] text-white p-3 rounded-full hover:scale-110 transition-transform shadow-lg">
          <Plus size={24} />
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {pets.map(pet => (
          <div key={pet.id} className="bg-[var(--bg-surface)] rounded-card border border-[var(--border-color)] overflow-hidden shadow-sm flex flex-col sm:flex-row">
            <div className="w-full sm:w-40 h-40 bg-[var(--accent)] opacity-20 flex items-center justify-center">
              {/* Aquí iría la foto de la mascota desde pyg-owner */}
              <Activity size={40} className="text-[var(--accent)]" />
            </div>
            <div className="p-6 flex-1">
              <div className="flex justify-between items-start mb-2">
                <h4 className="text-xl font-bold text-[var(--text-main)]">{pet.name}</h4>
                <span className={`text-xs px-2 py-1 rounded-lg font-bold ${pet.status === 'Al día' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                  {pet.status}
                </span>
              </div>
              <p className="text-[var(--text-muted)] text-sm mb-4">{pet.breed}</p>
              
              <div className="flex gap-4 text-xs font-semibold text-[var(--text-muted)]">
                <div className="flex items-center gap-1"><Calendar size={14}/> {pet.lastCheck}</div>
                <div className="flex items-center gap-1"><Heart size={14} className="text-red-400"/> Salud OK</div>
              </div>

              <button className="mt-4 w-full py-2 bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl text-sm font-bold hover:bg-[var(--color-terracotta)] hover:text-white transition-all">
                Ver Ficha Completa
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default MyPetsPage;