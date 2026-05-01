import { User, Mail, MapPin, Plus, Settings, Cat, Dog} from 'lucide-react';

const OwnerProfilePage = () => {
  // Simulación de datos que vendrán de pyg-owner
  const ownerData = {
    name: "María Ignacia",
    email: "maria@example.com",
    location: "Maule, Linares",
    pets: [
      { id: 1, name: "Luna", type: "Gato", breed: "Siamés", age: "2 años" },
      { id: 2, name: "Thor", type: "Perro", breed: "Labrador", age: "4 años" }
    ]
  };

  return (
    <div className="max-w-6xl mx-auto p-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">

        {/* Columna Izquierda: Información Personal */}
        <div className="md:col-span-1 space-y-6">
          <div className="bg-[var(--bg-surface)] p-8 rounded-card border border-[var(--border-color)] shadow-[var(--card-shadow)] text-center">
            <div className="w-32 h-32 bg-[var(--accent)] rounded-full mx-auto mb-4 border-4 border-[var(--bg-main)] flex items-center justify-center shadow-lg">
              <User size={60} className="text-white" />
            </div>
            <h2 className="text-2xl font-bold text-[var(--text-main)]">{ownerData.name}</h2>
            <p className="text-[var(--text-muted)] text-sm mb-6">Dueño de Mascotas</p>

            <div className="space-y-4 text-left">
              <div className="flex items-center gap-3 text-[var(--text-muted)] bg-[var(--bg-main)] p-3 rounded-2xl border border-[var(--border-color)]">
                <Mail size={18} className="text-[var(--color-terracotta)]" />
                <span className="text-sm truncate">{ownerData.email}</span>
              </div>
              <div className="flex items-center gap-3 text-[var(--text-muted)] bg-[var(--bg-main)] p-3 rounded-2xl border border-[var(--border-color)]">
                <MapPin size={18} className="text-[var(--color-terracotta)]" />
                <span className="text-sm">{ownerData.location}</span>
              </div>
            </div>

            <button className="w-full mt-6 flex items-center justify-center gap-2 text-sm font-bold text-[var(--text-muted)] hover:text-[var(--color-terracotta)] transition-colors">
              <Settings size={16} /> Editar Perfil
            </button>
          </div>
        </div>

        {/* Columna Derecha: Resumen de Mascotas */}
        <div className="md:col-span-2 space-y-6">
          <div className="flex justify-between items-end mb-2">
            <div>
              <h3 className="text-3xl font-black text-[var(--text-main)]">Mis <span className="text-[var(--color-terracotta)]">Pequeños</span></h3>
              <p className="text-[var(--text-muted)]">Resumen de tus compañeros registrados</p>
            </div>
            <button className="flex items-center gap-2 bg-[var(--accent)] text-white px-5 py-2.5 rounded-xl font-bold hover:opacity-90 transition-all shadow-md">
              <Plus size={18} /> Nueva Mascota
            </button>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {ownerData.pets.map(pet => (
              <div key={pet.id} className="bg-[var(--bg-surface)] p-6 rounded-card border border-[var(--border-color)] shadow-[var(--card-shadow)] group hover:border-[var(--color-terracotta)] transition-all">
                <div className="flex justify-between items-start mb-4">
                <div className={`p-3 rounded-2xl ${pet.type === 'Gato' ? 'bg-[#fef3c7] text-[#92400e]' : 'bg-[#dcfce7] text-[#166534]'}`}>
                    {pet.type === 'Gato' ? <Cat size={24} /> : <Dog size={24} />}
                </div>
                  <span className="text-xs font-bold bg-[var(--bg-main)] text-[var(--text-muted)] px-3 py-1 rounded-full border border-[var(--border-color)]">
                    {pet.age}
                  </span>
                </div>
                <h4 className="text-xl font-bold text-[var(--text-main)] mb-1">{pet.name}</h4>
                <p className="text-[var(--text-muted)] text-sm">{pet.type} • {pet.breed}</p>

                <div className="mt-6 pt-4 border-t border-[var(--border-color)]">
                  <button className="text-sm font-bold text-[var(--color-terracotta)] hover:underline">Ver ficha médica</button>
                </div>
              </div>
            ))}
          </div>
        </div>

      </div>
    </div>
  );
};

export default OwnerProfilePage;