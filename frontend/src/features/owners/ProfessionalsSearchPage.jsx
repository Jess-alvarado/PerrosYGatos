import { Star, Award, Search } from 'lucide-react';

const ProfessionalsSearchPage = () => {
  const proList = [
    { id: 1, name: "Dr. Ricardo Soto", spec: "Etólogo Canino", rating: 4.9, price: "$25.000" },
    { id: 2, name: "Dra. Ana Luz", spec: "Especialista Felina", rating: 4.8, price: "$30.000" }
  ];

  return (
    <div className="max-w-6xl mx-auto p-8 animate-in fade-in duration-500">
      <div className="mb-10 text-center">
        <h3 className="text-4xl font-black text-[var(--text-main)] mb-4">
          Encuentra al <span className="text-[var(--accent)]">experto ideal</span>
        </h3>
        <div className="max-w-2xl mx-auto relative">
          <Search className="absolute left-4 top-4 text-[var(--text-muted)]" />
          <input
            type="text"
            placeholder="Buscar por especialidad (ej: Ansiedad, Cachorros...)"
            className="w-full pl-12 pr-4 py-4 bg-[var(--bg-surface)] border border-[var(--border-color)] rounded-2xl shadow-sm outline-none focus:ring-2 focus:ring-[var(--accent)] text-[var(--text-main)]"
          />
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {proList.map(pro => (
          <div key={pro.id} className="bg-[var(--bg-surface)] p-6 rounded-card border border-[var(--border-color)] shadow-sm hover:-translate-y-1 transition-all">
            <div className="w-16 h-16 bg-[var(--color-peach)] rounded-2xl mb-4 flex items-center justify-center">
              <Award size={32} className="text-[var(--color-terracotta)]" />
            </div>
            <h4 className="text-lg font-bold text-[var(--text-main)]">{pro.name}</h4>
            <p className="text-[var(--accent)] text-sm font-semibold mb-3">{pro.spec}</p>
            
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-1 text-yellow-500 font-bold">
                <Star size={16} fill="currentColor" /> {pro.rating}
              </div>
              <span className="text-[var(--text-main)] font-black">{pro.price}</span>
            </div>

            <button className="w-full py-3 bg-[var(--color-terracotta)] text-white rounded-xl font-bold shadow-md hover:opacity-90 transition-all">
              Agendar Sesión
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ProfessionalsSearchPage;