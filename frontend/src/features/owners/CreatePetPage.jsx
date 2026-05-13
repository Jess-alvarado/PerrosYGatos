import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft, Save } from "lucide-react";
import axios from "axios";

const CreatePetPage = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    type: "DOG", // Default
    breed: "",
    age: "",
    sterilized: false,
    sex: "male",
    behaviorDescription: "",
  });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const token = localStorage.getItem("token");
      const payload = {
        ...formData,
        age: parseInt(formData.age), // Aseguramos que sea número
      };

      await axios.post("http://localhost:9090/api/pets", payload, {
        headers: { Authorization: `Bearer ${token}` },
      });

      // Redirigir de vuelta al perfil tras éxito
      navigate("/profile");
    } catch (error) {
      console.error("Error al crear mascota:", error);
      alert("No se pudo registrar la mascota. Revisa la consola.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto p-8">
      <button
        onClick={() => navigate(-1)}
        className="flex items-center gap-2 text-[var(--text-muted)] hover:text-[var(--text-main)] mb-6 transition-colors"
      >
        <ArrowLeft size={18} /> Volver
      </button>

      <div className="bg-[var(--bg-surface)] p-8 rounded-card border border-[var(--border-color)] shadow-[var(--card-shadow)]">
        <h2 className="text-3xl font-black text-[var(--text-main)] mb-2">
          Registrar{" "}
          <span className="text-[var(--color-terracotta)]">Mascota</span>
        </h2>
        <p className="text-[var(--text-muted)] mb-8 text-sm">
          Cuéntanos sobre tu nuevo compañero.
        </p>

        <form onSubmit={handleSubmit} className="space-y-5">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold uppercase mb-2 ml-1 text-[var(--text-main)]">
                Nombre
              </label>
              <input
                required
                name="name"
                value={formData.name}
                onChange={handleChange}
                className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-3 px-4 outline-none focus:ring-2 focus:ring-[var(--accent)]"
                placeholder="Ej: Pelón"
              />
            </div>
            <div>
              <label className="block text-xs font-bold uppercase mb-2 ml-1 text-[var(--text-main)]">
                Tipo
              </label>
              <select
                name="type"
                value={formData.type}
                onChange={handleChange}
                className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-3 px-4 outline-none focus:ring-2 focus:ring-[var(--accent)]"
              >
                <option value="DOG">Perro</option>
                <option value="CAT">Gato</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold uppercase mb-2 ml-1 text-[var(--text-main)]">
                Raza
              </label>
              <input
                required
                name="breed"
                value={formData.breed}
                onChange={handleChange}
                className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-3 px-4 outline-none focus:ring-2 focus:ring-[var(--accent)]"
                placeholder="Ej: Pastor Alemán"
              />
            </div>
            <div>
              <label className="block text-xs font-bold uppercase mb-2 ml-1 text-[var(--text-main)]">
                Edad
              </label>
              <input
                required
                type="number"
                name="age"
                value={formData.age}
                onChange={handleChange}
                className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-3 px-4 outline-none focus:ring-2 focus:ring-[var(--accent)]"
                placeholder="Ej: 8"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold uppercase mb-2 ml-1 text-[var(--text-main)]">
                Sexo
              </label>
              <select
                name="sex"
                value={formData.sex}
                onChange={handleChange}
                className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-3 px-4 outline-none focus:ring-2 focus:ring-[var(--accent)]"
              >
                <option value="male">Macho</option>
                <option value="female">Hembra</option>
              </select>
            </div>
            <div className="flex items-end pb-3 pl-2">
              <label className="flex items-center gap-3 cursor-pointer">
                <input
                  type="checkbox"
                  name="sterilized"
                  checked={formData.sterilized}
                  onChange={handleChange}
                  className="w-5 h-5 rounded border-[var(--border-color)] text-[var(--color-terracotta)] focus:ring-[var(--color-terracotta)]"
                />
                <span className="text-sm font-bold text-[var(--text-main)] uppercase">
                  ¿Está esterilizado?
                </span>
              </label>
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold uppercase mb-2 ml-1 text-[var(--text-main)]">
              Descripción del comportamiento
            </label>
            <textarea
              name="behaviorDescription"
              value={formData.behaviorDescription}
              onChange={handleChange}
              rows="3"
              className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-3 px-4 outline-none focus:ring-2 focus:ring-[var(--accent)] resize-none"
              placeholder="Ej: Es muy juguetón, pero tira de la correa al pasear..."
            ></textarea>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-[var(--color-terracotta)] text-white font-bold py-4 rounded-2xl shadow-lg hover:opacity-90 transition-all flex items-center justify-center gap-2 disabled:opacity-50"
          >
            <Save size={20} />
            {loading ? "Registrando..." : "Guardar Mascota"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default CreatePetPage;
