import { useState } from "react";
import { Phone, MapPin, Calendar } from "lucide-react";
// Importamos tu instancia configurada con los interceptores automáticos
import api from "../../api/api";

// Recibimos onComplete como prop para avisar al componente padre cuando terminar
const OwnerProfileForm = ({ onComplete }) => {
  // Estado inicial con los campos exactos que espera tu Body de Java
  const [formData, setFormData] = useState({
    phone: "",
    address: "",
    birthDate: "",
  });

  const [loading, setLoading] = useState(false);

  // Manejador de cambios genérico
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      // Petición al Gateway usando tu cliente interceptado de Axios.
      // La URL base y el token 'Bearer' en las cabeceras se inyectan automáticamente.
      const response = await api.post("/api/owners/profile", formData);

      console.log("Perfil de dueño creado con éxito:", response.data);

      // Avisamos que terminamos el flujo de configuración inicial
      if (onComplete) {
        onComplete();
      }
    } catch (error) {
      console.error(
        "Error al guardar perfil de dueño:",
        error.response?.data || error.message,
      );
      alert(
        "No se pudo guardar el perfil. Revisa la consola para más detalles.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto bg-[var(--bg-surface)] p-8 rounded-card border border-[var(--border-color)] shadow-[var(--card-shadow)]">
      <h2 className="text-2xl font-bold text-[var(--text-main)] mb-6 text-center">
        Completa tu perfil de Dueño
      </h2>

      <form className="space-y-5" onSubmit={handleSubmit}>
        {/* CAMPO TELÉFONO */}
        <div>
          <label className="block text-sm font-semibold mb-2 ml-1">
            Teléfono
          </label>
          <div className="relative">
            <Phone className="absolute left-4 top-3.5 w-5 h-5 text-[var(--accent)]" />
            <input
              required
              name="phone"
              type="tel"
              placeholder="+56988888888"
              value={formData.phone}
              onChange={handleChange}
              className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-2xl py-3.5 pl-12 pr-4 text-[var(--text-main)] outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
            />
          </div>
        </div>

        {/* CAMPO DIRECCIÓN */}
        <div>
          <label className="block text-sm font-semibold mb-2 ml-1">
            Dirección
          </label>
          <div className="relative">
            <MapPin className="absolute left-4 top-3.5 w-5 h-5 text-[var(--accent)]" />
            <input
              required
              name="address"
              type="text"
              placeholder="Av. Los Perros 404, Santiago"
              value={formData.address}
              onChange={handleChange}
              className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-2xl py-3.5 pl-12 pr-4 text-[var(--text-main)] outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
            />
          </div>
        </div>

        {/* CAMPO FECHA DE NACIMIENTO */}
        <div>
          <label className="block text-sm font-semibold mb-2 ml-1">
            Fecha de Nacimiento
          </label>
          <div className="relative">
            <Calendar className="absolute left-4 top-3.5 w-5 h-5 text-[var(--accent)]" />
            <input
              required
              name="birthDate"
              type="date"
              value={formData.birthDate}
              onChange={handleChange}
              className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-2xl py-3.5 pl-12 pr-4 text-[var(--text-main)] outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
            />
          </div>
        </div>

        <button
          type="submit"
          disabled={loading}
          className={`w-full bg-[var(--color-terracotta)] text-white font-bold py-4 rounded-2xl shadow-lg hover:opacity-90 transition-all mt-4 transform active:scale-95 ${loading ? "opacity-50 cursor-not-allowed" : ""}`}
        >
          {loading ? "Guardando..." : "Guardar en Perros&Gatos"}
        </button>
      </form>
    </div>
  );
};

export default OwnerProfileForm;
