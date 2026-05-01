import { Filter, MapPin, PawPrint } from 'lucide-react';

const PetDirectoryPage = () => {
  return (
    <div className="max-w-7xl mx-auto p-8 animate-in fade-in duration-500">
      <div className="flex flex-col md:flex-row justify-between items-center mb-8 gap-4">
        <h3 className="text-3xl font-black text-[var(--text-main)]">
          Casos <span className="text-[var(--color-terracotta)]">Clínicos</span>
        </h3>
        
        {/* Barra de Filtros */}
        <div className="flex flex-wrap gap-3 bg-[var(--bg-surface)] p-2 rounded-2xl border border-[var(--border-color)] shadow-sm">
          <div className="relative">
            <MapPin size={16} className="absolute left-3 top-3 text-[var(--text-muted)]" />
            <select className="pl-9 pr-4 py-2 bg-transparent text-sm outline-none text-[var(--text-main)]">
              <option>Toda la Región</option>
              <option>Maule</option>
            </select>
          </div>
          <div className="w-[1px] bg-[var(--border-color)] h-8 self-center" />
          <select className="px-4 py-2 bg-transparent text-sm outline-none text-[var(--text-main)]">
            <option>Especie: Todas</option>
            <option>Solo Perros</option>
            <option>Solo Gatos</option>
          </select>
          <button className="bg-[var(--accent)] text-white px-6 py-2 rounded-xl text-sm font-bold flex items-center gap-2">
            <Filter size={16} /> Filtrar
          </button>
        </div>
      </div>

      {/* Grid de mascotas (Aquí irían los resultados de tu backend pyg-owner) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 opacity-60 italic text-center py-20 border-2 border-dashed border-[var(--border-color)] rounded-3xl">
        <div className="col-span-full">
          <PawPrint size={48} className="mx-auto mb-4 text-[var(--text-muted)]" />
          <p className="text-[var(--text-muted)]">Conectando con pyg-owner para traer los casos...</p>
        </div>
      </div>
    </div>
  );
};

export default PetDirectoryPage;