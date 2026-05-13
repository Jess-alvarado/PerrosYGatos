import { useEffect, useState } from "react";
import {
  User,
  Briefcase,
  Award,
  MapPin,
  Star,
  MessageSquare,
  Clock,
  Phone,
  Calendar,
  Dog,
  Edit3,
  DollarSign,
} from "lucide-react";
import api from "../../api/api";

const ProfessionalProfilePage = () => {
  const [profData, setProfData] = useState(null);
  const [loading, setLoading] = useState(true);

  const userSession = JSON.parse(localStorage.getItem("user")) || {};

  useEffect(() => {
    const fetchProfessionalData = async () => {
      try {
        const response = await api.get("/api/professionals/profile");
        setProfData(response.data);
      } catch (error) {
        console.error("Error al cargar datos profesionales:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchProfessionalData();
  }, []);

  const handleEdit = () => {
    // Aquí iría tu lógica para abrir el modal o navegar a la edición
    console.log("Navegando a edición...");
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px] text-[var(--text-muted)] animate-pulse font-medium">
        Cargando perfil profesional...
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto p-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        {/* Columna Izquierda: Tarjeta Profesional con Detalles */}
        <div className="md:col-span-1 space-y-6">
          <div className="bg-[var(--bg-surface)] rounded-card border border-[var(--border-color)] shadow-[var(--card-shadow)] overflow-hidden relative">
            <div className="h-2 bg-[var(--color-terracotta)] w-full"></div>

            {/* Botón Editar Flotante */}
            <button
              onClick={handleEdit}
              className="absolute top-4 right-4 p-2 bg-[var(--bg-main)] text-[var(--text-muted)] hover:text-[var(--color-terracotta)] rounded-full border border-[var(--border-color)] transition-colors shadow-sm"
              title="Editar Perfil"
            >
              <Edit3 size={18} />
            </button>

            <div className="p-8">
              {/* Avatar */}
              <div className="w-28 h-28 bg-[var(--accent)] rounded-full mx-auto mb-4 border-4 border-[var(--bg-main)] flex items-center justify-center shadow-lg relative">
                <User size={50} className="text-white" />
                <div className="absolute bottom-1 right-1 bg-[var(--color-terracotta)] p-1.5 rounded-full border-2 border-[var(--bg-surface)]">
                  <Award size={14} className="text-white" />
                </div>
              </div>

              {/* Info Principal */}
              <div className="text-center mb-6">
                <h2 className="text-2xl font-bold text-[var(--text-main)] leading-tight">
                  {userSession.firstname} {userSession.lastname}
                </h2>
                <p className="text-[var(--color-terracotta)] font-bold text-sm uppercase tracking-wide mt-1">
                  {profData?.title || "Profesional"}
                </p>

                <div className="flex justify-center items-center gap-1 mt-3">
                  {[...Array(5)].map((_, i) => (
                    <Star
                      key={i}
                      size={14}
                      className="fill-[var(--color-peach)] text-[var(--color-peach)]"
                    />
                  ))}
                  <span className="text-[10px] font-black bg-[var(--bg-main)] px-2 py-0.5 rounded-full text-[var(--text-muted)] ml-2 border border-[var(--border-color)]">
                    NUEVO
                  </span>
                </div>
              </div>

              {/* Lista de Detalles Técnica */}
              <div className="space-y-3 pt-6 border-t border-[var(--border-color)]">
                <DetailRow
                  icon={<Briefcase size={16} />}
                  label="Profesión"
                  value={profData?.profession}
                />
                <DetailRow
                  icon={<MapPin size={16} />}
                  label="Ubicación"
                  value={profData?.address}
                />
                <DetailRow
                  icon={<Phone size={16} />}
                  label="Contacto"
                  value={profData?.phone}
                />
                <DetailRow
                  icon={<Clock size={16} />}
                  label="Experiencia"
                  value={profData?.experience}
                />
                <DetailRow
                  icon={<Calendar size={16} />}
                  label="Horario"
                  value={profData?.availability}
                />
                <DetailRow
                  icon={<Dog size={16} />}
                  label="Atiende a"
                  value={profData?.petTypes}
                />
                <div className="flex items-center justify-between p-3 bg-[var(--bg-main)] rounded-xl border border-[var(--border-color)] mt-4">
                  <div className="flex items-center gap-2 text-[var(--text-muted)]">
                    <DollarSign
                      size={16}
                      className="text-[var(--color-terracotta)]"
                    />
                    <span className="text-xs font-bold uppercase">
                      Consulta
                    </span>
                  </div>
                  <span className="font-black text-[var(--text-main)] text-sm">
                    ${profData?.price || "0"}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Columna Derecha: Bio y Estadísticas */}
        <div className="md:col-span-2 space-y-6">
          <div className="bg-[var(--bg-surface)] p-8 rounded-card border border-[var(--border-color)] shadow-[var(--card-shadow)]">
            <h3 className="text-xl font-bold text-[var(--text-main)] mb-4 flex items-center gap-2">
              <MessageSquare
                size={20}
                className="text-[var(--color-terracotta)]"
              />
              Reseña Profesional
            </h3>
            <p className="text-[var(--text-muted)] leading-relaxed italic text-sm md:text-base">
              {profData?.bio ||
                "Describe aquí tu formación y pasión por los animales."}
            </p>
          </div>

          <div className="grid grid-cols-2 gap-6">
            <StatCard label="Atenciones" value="0" color="var(--accent)" />
            <StatCard
              label="Valoración"
              value="5.0"
              color="var(--color-terracotta)"
            />
          </div>

          <button className="w-full bg-[var(--color-terracotta)] hover:opacity-90 text-white font-bold py-4 rounded-2xl transition-all flex items-center justify-center gap-3 shadow-lg transform active:scale-[0.98]">
            <Calendar size={20} />
            Gestionar Agenda de Consultas
          </button>
        </div>
      </div>
    </div>
  );
};

/* Componentes Pequeños para Limpieza Visual */
const DetailRow = ({ icon, label, value }) => (
  <div className="flex items-center justify-between text-sm">
    <div className="flex items-center gap-2 text-[var(--text-muted)]">
      <span className="text-[var(--accent)]">{icon}</span>
      <span className="font-medium text-[12px]">{label}</span>
    </div>
    <span className="text-[var(--text-main)] font-semibold text-[13px] truncate max-w-[120px]">
      {value || "No definido"}
    </span>
  </div>
);

const StatCard = ({ label, value, color }) => (
  <div className="bg-[var(--bg-surface)] p-6 rounded-card border border-[var(--border-color)] shadow-[var(--card-shadow)] text-center">
    <h4 className="text-3xl font-black" style={{ color }}>
      {value}
    </h4>
    <p className="text-[var(--text-muted)] text-[10px] font-black uppercase tracking-[2px] mt-1">
      {label}
    </p>
  </div>
);

export default ProfessionalProfilePage;
