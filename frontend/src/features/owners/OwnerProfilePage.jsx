import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  User,
  Mail, // <--- Se usa en la info del dueño
  MapPin, // <--- Se usa en la dirección
  Plus, // <--- Se usa en el botón de agregar
  Settings, // <--- Se usa en el botón de editar perfil
  Cat, // <--- Se usa en la lista y modal
  Dog, // <--- Se usa en la lista y modal
  AlertCircle, // <--- Se usa en el banner de error
  X, // <--- Se usa para cerrar el modal
  Edit2, // <--- Se usa dentro del modal
  Trash2, // <--- Se usa dentro del modal
} from "lucide-react";
import api from "../../api/api";

const OwnerProfilePage = () => {
  const [ownerData, setOwnerData] = useState(null);
  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedPet, setSelectedPet] = useState(null);

  const navigate = useNavigate();
  const userSession = JSON.parse(localStorage.getItem("user")) || {};

  useEffect(() => {
    const fetchAllData = async () => {
      try {
        setLoading(true);
        setError(null);
        const token = localStorage.getItem("token");
        const headers = { Authorization: `Bearer ${token}` };

        const [ownerResponse, petsResponse] = await Promise.all([
          api.get("/api/owners/profile", { headers }),
          api.get("/api/pets", { headers }),
        ]);

        setOwnerData(ownerResponse.data);
        setPets(petsResponse.data);
      } catch (err) {
        console.error(err);
        setError("Error al cargar los datos. Intenta recargar la página.");
      } finally {
        setLoading(false);
      }
    };
    fetchAllData();
  }, []);

  // USO DE 'loading': Si es true, mostramos pantalla de carga
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="flex flex-col items-center gap-4">
          <div className="w-12 h-12 border-4 border-[var(--color-terracotta)] border-t-transparent rounded-full animate-spin"></div>
          <p className="text-[var(--text-muted)] font-medium">Cargando...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto p-8 animate-in fade-in duration-500">
      {/* USO DE 'error' y 'AlertCircle' */}
      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 text-red-600 rounded-2xl flex items-center gap-3">
          <AlertCircle size={20} />
          <p className="text-sm font-semibold">{error}</p>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        {/* COLUMNA PERFIL */}
        <div className="md:col-span-1 space-y-6">
          <div className="bg-[var(--bg-surface)] p-8 rounded-card border border-[var(--border-color)] shadow-sm text-center">
            <div className="w-28 h-28 bg-[var(--accent)] rounded-full mx-auto mb-4 flex items-center justify-center text-white">
              <User size={50} />
            </div>
            <h2 className="text-2xl font-bold text-[var(--text-main)]">
              {ownerData?.firstName || userSession.firstname}
            </h2>

            <div className="mt-6 space-y-3">
              {/* USO DE 'Mail' */}
              <div className="flex items-center gap-3 p-3 bg-[var(--bg-main)] rounded-xl border border-[var(--border-color)]">
                <Mail size={18} className="text-[var(--color-terracotta)]" />
                <span className="text-sm truncate">
                  {ownerData?.email || userSession.email}
                </span>
              </div>
              {/* USO DE 'MapPin' */}
              <div className="flex items-center gap-3 p-3 bg-[var(--bg-main)] rounded-xl border border-[var(--border-color)]">
                <MapPin size={18} className="text-[var(--color-terracotta)]" />
                <span className="text-sm">
                  {ownerData?.address || "Sin dirección"}
                </span>
              </div>
            </div>

            {/* USO DE 'Settings' */}
            <button
              onClick={() => navigate("/profile/edit")}
              className="w-full mt-6 flex items-center justify-center gap-2 text-sm font-bold text-[var(--text-muted)] hover:text-[var(--color-terracotta)] transition-colors"
            >
              <Settings size={16} /> Configurar Perfil
            </button>
          </div>
        </div>

        {/* COLUMNA MASCOTAS */}
        <div className="md:col-span-2 space-y-6">
          <div className="flex justify-between items-center">
            <h3 className="text-3xl font-black text-[var(--text-main)]">
              Mis Mascotas
            </h3>
            {/* USO DE 'Plus' */}
            <button
              onClick={() => navigate("/pets/create")}
              className="bg-[var(--accent)] text-white p-3 rounded-xl hover:scale-105 transition-transform"
            >
              <Plus size={24} />
            </button>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {pets.map((pet) => (
              <div
                key={pet.id}
                onClick={() => setSelectedPet(pet)}
                className="group cursor-pointer bg-[var(--bg-surface)] p-6 rounded-card border border-[var(--border-color)] hover:border-[var(--color-terracotta)] transition-all shadow-sm"
              >
                <div className="flex items-center gap-4">
                  <div className="p-3 bg-[var(--bg-main)] rounded-2xl group-hover:bg-orange-50 transition-colors">
                    {/* USO DE 'Cat' y 'Dog' */}
                    {pet.type?.toLowerCase() === "gato" ? (
                      <Cat size={24} className="text-orange-500" />
                    ) : (
                      <Dog size={24} className="text-blue-500" />
                    )}
                  </div>
                  <div>
                    <h4 className="font-bold text-lg">{pet.name}</h4>
                    <p className="text-sm text-[var(--text-muted)]">
                      {pet.breed}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* MODAL CON 'X', 'Edit2' y 'Trash2' */}
      {selectedPet && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-[var(--bg-surface)] w-full max-w-sm rounded-[2rem] p-8 relative animate-in zoom-in-95 duration-200">
            <button
              onClick={() => setSelectedPet(null)}
              className="absolute top-6 right-6 text-[var(--text-muted)] hover:text-black"
            >
              <X size={24} />
            </button>

            <div className="text-center space-y-4">
              <div className="w-20 h-20 bg-[var(--bg-main)] rounded-3xl mx-auto flex items-center justify-center">
                {selectedPet.type?.toLowerCase() === "gato" ? (
                  <Cat size={40} />
                ) : (
                  <Dog size={40} />
                )}
              </div>
              <h3 className="text-2xl font-bold">{selectedPet.name}</h3>
              <div className="flex justify-center gap-2">
                <span className="px-3 py-1 bg-orange-100 text-orange-600 rounded-full text-xs font-bold uppercase">
                  {selectedPet.type}
                </span>
                <span className="px-3 py-1 bg-gray-100 text-gray-600 rounded-full text-xs font-bold uppercase">
                  {selectedPet.age} AÑOS
                </span>
              </div>
            </div>

            <div className="mt-8 grid grid-cols-2 gap-3">
              <button
                onClick={() => navigate(`/pets/edit/${selectedPet.id}`)}
                className="flex items-center justify-center gap-2 bg-[var(--text-main)] text-white py-3 rounded-2xl font-bold text-sm"
              >
                <Edit2 size={16} /> Editar
              </button>
              <button className="flex items-center justify-center gap-2 bg-red-50 text-red-500 py-3 rounded-2xl font-bold text-sm hover:bg-red-100">
                <Trash2 size={16} /> Borrar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default OwnerProfilePage;
