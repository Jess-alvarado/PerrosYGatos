import { User, Briefcase, Award, MapPin, Star, MessageSquare, Clock } from 'lucide-react';

const ProfessionalProfilePage = () => {
  // Simulación de datos de pyg-professional
  const profData = {
    name: "Dr. Javier S.",
    title: "Etólogo Clínico Especialista",
    location: "Linares, Chile",
    experience: "5 años",
    rating: 4.9,
    bio: "Especialista en comportamiento felino y canino. Ayudo a fortalecer el vínculo entre humanos y mascotas mediante refuerzo positivo.",
    stats: {
      cases: 120,
      reviews: 85
    }
  };

  return (
    <div className="max-w-6xl mx-auto p-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">

        {/* Columna Izquierda: Tarjeta Profesional */}
        <div className="md:col-span-1 space-y-6">
          <div className="bg-[var(--bg-surface)] p-8 rounded-card border border-[var(--border-color)] shadow-[var(--card-shadow)] text-center relative overflow-hidden">
            {/* Decoración de fondo */}
            <div className="absolute top-0 left-0 w-full h-2 bg-[var(--color-terracotta)]"></div>

            <div className="w-32 h-32 bg-[var(--accent)] rounded-full mx-auto mb-4 border-4 border-[var(--bg-main)] flex items-center justify-center shadow-lg relative z-10">
              <User size={60} className="text-white" />
              <div className="absolute bottom-0 right-0 bg-[var(--color-terracotta)] p-2 rounded-full border-2 border-[var(--bg-surface)]">
                <Award size={16} className="text-white" />
              </div>
            </div>

            <h2 className="text-2xl font-bold text-[var(--text-main)]">{profData.name}</h2>
            <p className="text-[var(--color-terracotta)] font-bold text-sm mb-4">{profData.title}</p>

            <div className="flex justify-center items-center gap-1 mb-6">
              {[...Array(5)].map((_, i) => (
                <Star key={i} size={16} className={i < 4 ? "fill-[var(--color-peach)] text-[var(--color-peach)]" : "text-[var(--text-muted)]"} />
              ))}
              <span className="text-xs font-bold text-[var(--text-muted)] ml-2">({profData.rating})</span>
            </div>

            <div className="space-y-3">
              <div className="flex items-center gap-3 text-[var(--text-muted)] bg-[var(--bg-main)] p-3 rounded-2xl border border-[var(--border-color)]">
                <MapPin size={18} className="text-[var(--accent)]" />
                <span className="text-sm">{profData.location}</span>
              </div>
              <div className="flex items-center gap-3 text-[var(--text-muted)] bg-[var(--bg-main)] p-3 rounded-2xl border border-[var(--border-color)]">
                <Clock size={18} className="text-[var(--accent)]" />
                <span className="text-sm">{profData.experience} de experiencia</span>
              </div>
            </div>
          </div>
        </div>

        {/* Columna Derecha: Bio y Estadísticas */}
        <div className="md:col-span-2 space-y-6">
          <div className="bg-[var(--bg-surface)] p-8 rounded-card border border-[var(--border-color)] shadow-[var(--card-shadow)]">
            <h3 className="text-xl font-bold text-[var(--text-main)] mb-4 flex items-center gap-2">
              <Briefcase size={20} className="text-[var(--color-terracotta)]" />
              Sobre mí
            </h3>
            <p className="text-[var(--text-muted)] leading-relaxed">
              {profData.bio}
            </p>
          </div>

          <div className="grid grid-cols-2 gap-6">
            <div className="bg-[var(--bg-surface)] p-6 rounded-card border border-[var(--border-color)] shadow-[var(--card-shadow)] text-center">
              <h4 className="text-3xl font-black text-[var(--accent)]">{profData.stats.cases}</h4>
              <p className="text-[var(--text-muted)] text-sm font-bold uppercase tracking-wider">Casos Resueltos</p>
            </div>
            <div className="bg-[var(--bg-surface)] p-6 rounded-card border border-[var(--border-color)] shadow-[var(--card-shadow)] text-center">
              <h4 className="text-3xl font-black text-[var(--color-terracotta)]">{profData.stats.reviews}</h4>
              <p className="text-[var(--text-muted)] text-sm font-bold uppercase tracking-wider">Opiniones</p>
            </div>
          </div>

          <button className="w-full bg-[var(--bg-surface)] hover:bg-[var(--bg-main)] text-[var(--text-main)] font-bold py-4 rounded-2xl border-2 border-[var(--color-terracotta)] transition-all flex items-center justify-center gap-3 shadow-md">
            <MessageSquare size={20} className="text-[var(--color-terracotta)]" />
            Configurar disponibilidad y precios
          </button>
        </div>

      </div>
    </div>
  );
};

export default ProfessionalProfilePage;