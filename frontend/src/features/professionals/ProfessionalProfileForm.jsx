import { useState } from "react";
import {
  Briefcase,
  Award,
  DollarSign,
  Phone,
  MapPin,
  Calendar,
  Dog,
} from "lucide-react";
import api from "../../api/api";

const ProfessionalProfileForm = ({ onComplete }) => {
  const [formData, setFormData] = useState({
    profession: "",
    specialty: "",
    experienceYears: "",
    price: "",
    bio: "",
    phone: "",
    address: "",
    availability: "",
    petTypes: "",
  });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await api.post("/api/professionals/profile", {
        profession: formData.profession,
        title: formData.specialty,
        experience: `${formData.experienceYears} años`,
        price: formData.price,
        bio: formData.bio,
        phone: formData.phone,
        address: formData.address,
        availability: formData.availability,
        petTypes: formData.petTypes,
      });

      console.log("Datos guardados con éxito en pyg-professional");
      if (onComplete) onComplete();
    } catch (error) {
      console.error("Error al guardar perfil profesional:", error);
      alert(
        "Error: " +
          (error.response?.data?.message || "No se pudo guardar el perfil"),
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-xl mx-auto bg-[var(--bg-surface)] p-8 rounded-card border border-[var(--border-color)] shadow-[var(--card-shadow)] mb-10">
      <h2 className="text-2xl font-bold text-[var(--text-main)] mb-2 text-center">
        Registro Profesional
      </h2>
      <p className="text-[var(--text-muted)] text-sm text-center mb-8">
        Completa tu perfil para ofrecer servicios
      </p>

      <form className="space-y-5" onSubmit={handleSubmit}>
        {/* Profesión y Título */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-bold mb-1.5 ml-1 text-[var(--text-main)] uppercase">
              Profesión General
            </label>
            <div className="relative">
              <Briefcase className="absolute left-4 top-3 w-4 h-4 text-[var(--accent)]" />
              <input
                required
                name="profession"
                value={formData.profession}
                onChange={handleChange}
                placeholder="Ej: Veterinario"
                className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-2.5 pl-11 pr-4 text-sm outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
              />
            </div>
          </div>
          <div>
            <label className="block text-xs font-bold mb-1.5 ml-1 text-[var(--text-main)] uppercase">
              Especialidad (Título)
            </label>
            <div className="relative">
              <Award className="absolute left-4 top-3 w-4 h-4 text-[var(--accent)]" />
              <input
                required
                name="specialty"
                value={formData.specialty}
                onChange={handleChange}
                placeholder="Ej: Etólogo Clínico"
                className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-2.5 pl-11 pr-4 text-sm outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
              />
            </div>
          </div>
        </div>

        {/* Teléfono y Dirección */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-bold mb-1.5 ml-1 text-[var(--text-main)] uppercase">
              Teléfono de Contacto
            </label>
            <div className="relative">
              <Phone className="absolute left-4 top-3 w-4 h-4 text-[var(--accent)]" />
              <input
                required
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                placeholder="+56 9..."
                className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-2.5 pl-11 pr-4 text-sm outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
              />
            </div>
          </div>
          <div>
            <label className="block text-xs font-bold mb-1.5 ml-1 text-[var(--text-main)] uppercase">
              Dirección / Ciudad
            </label>
            <div className="relative">
              <MapPin className="absolute left-4 top-3 w-4 h-4 text-[var(--accent)]" />
              <input
                required
                name="address"
                value={formData.address}
                onChange={handleChange}
                placeholder="Ej: Linares, Centro"
                className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-2.5 pl-11 pr-4 text-sm outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
              />
            </div>
          </div>
        </div>

        {/* Disponibilidad y Mascotas */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-bold mb-1.5 ml-1 text-[var(--text-main)] uppercase">
              Disponibilidad
            </label>
            <div className="relative">
              <Calendar className="absolute left-4 top-3 w-4 h-4 text-[var(--accent)]" />
              <input
                required
                name="availability"
                value={formData.availability}
                onChange={handleChange}
                placeholder="Ej: Lunes a Viernes"
                className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-2.5 pl-11 pr-4 text-sm outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
              />
            </div>
          </div>
          <div>
            <label className="block text-xs font-bold mb-1.5 ml-1 text-[var(--text-main)] uppercase">
              Tipos de Mascota
            </label>
            <div className="relative">
              <Dog className="absolute left-4 top-3 w-4 h-4 text-[var(--accent)]" />
              <input
                required
                name="petTypes"
                value={formData.petTypes}
                onChange={handleChange}
                placeholder="Ej: Perros y Gatos"
                className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-2.5 pl-11 pr-4 text-sm outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
              />
            </div>
          </div>
        </div>

        {/* Experiencia y Precio */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-bold mb-1.5 ml-1 text-[var(--text-main)] uppercase">
              Años de Experiencia
            </label>
            <input
              required
              type="number"
              name="experienceYears"
              value={formData.experienceYears}
              onChange={handleChange}
              placeholder="Años"
              className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-2.5 px-4 text-sm outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
            />
          </div>
          <div>
            <label className="block text-xs font-bold mb-1.5 ml-1 text-[var(--text-main)] uppercase">
              Precio Consulta ($)
            </label>
            <div className="relative">
              <DollarSign className="absolute left-4 top-3 w-4 h-4 text-[var(--accent)]" />
              <input
                required
                name="price"
                value={formData.price}
                onChange={handleChange}
                placeholder="Ej: 25000"
                className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-2.5 pl-11 pr-4 text-sm outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
              />
            </div>
          </div>
        </div>

        <div>
          <label className="block text-xs font-bold mb-1.5 ml-1 text-[var(--text-main)] uppercase">
            Biografía Profesional
          </label>
          <textarea
            required
            name="bio"
            value={formData.bio}
            onChange={handleChange}
            placeholder="Cuéntanos sobre tu formación y metodología..."
            className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-3 px-4 text-sm outline-none h-28 resize-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
          ></textarea>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-[var(--color-terracotta)] text-white font-bold py-3.5 rounded-xl shadow-lg hover:opacity-90 transition-all transform active:scale-95 disabled:opacity-50"
        >
          {loading
            ? "Sincronizando con el servidor..."
            : "Activar Mi Perfil Profesional"}
        </button>
      </form>
    </div>
  );
};

export default ProfessionalProfileForm;
