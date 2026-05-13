import api from "../../api/api";
import { useState } from "react";
import { Mail, Lock, PawPrint } from "lucide-react";
import { jwtDecode } from "jwt-decode";
import { useNavigate } from "react-router-dom";

const RegisterPage = ({ onFinishAuth, onSwitchToLogin }) => {
  const [role, setRole] = useState("owner");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [firstname, setFirstname] = useState("");
  const [lastname, setLastname] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    const finalRole = role === "owner" ? "ROLE_OWNER" : "ROLE_PROFESSIONAL";

    try {
      const response = await api.post("/api/auth/register", {
        username: username,
        password: password,
        firstname: firstname,
        lastname: lastname,
        rolename: finalRole,
      });

      console.log("Registro exitoso:", response.data);

      const { accessToken, refreshToken } = response.data;

      if (accessToken) {
        localStorage.setItem("token", accessToken);
        localStorage.setItem("refreshToken", refreshToken);

        try {
          const decoded = jwtDecode(accessToken);

          const userObj = {
            firstname: decoded.firstname || firstname,
            lastname: decoded.lastname || lastname,
            email: decoded.sub || username,
            id: decoded.uid,
          };

          localStorage.setItem("user", JSON.stringify(userObj));
          console.log("Datos de usuario guardados tras el registro");
        } catch (decodeError) {
          console.error("Error decodificando token en registro:", decodeError);
        }
      }

      alert("¡Cuenta creada con éxito!");

      if (onFinishAuth) {
        onFinishAuth(finalRole);
      }
      navigate("/onboarding");
    } catch (error) {
      console.error("Error en registro:", error);
      alert(
        error.response?.data?.message ||
          "Error al registrar. Revisa los datos.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-[var(--bg-main)] p-4">
      <div className="w-full max-w-md bg-[var(--bg-surface)] rounded-card shadow-[var(--card-shadow)] p-10 border border-[var(--border-color)]">
        <div className="flex flex-col items-center mb-6">
          <div className="bg-[var(--color-terracotta)] p-3 rounded-2xl mb-4 shadow-lg shadow-terracotta/20">
            <PawPrint className="text-white w-7 h-7" />
          </div>
          <h1 className="text-2xl font-bold text-[var(--text-main)]">
            Crear Cuenta
          </h1>
        </div>

        {/* Selector de Rol */}
        <div className="mb-6">
          <div className="grid grid-cols-2 gap-3 p-1 bg-[var(--bg-main)] rounded-2xl border border-[var(--border-color)]">
            <button
              type="button"
              onClick={() => setRole("owner")}
              className={`py-2.5 rounded-xl text-sm font-bold transition-all ${
                role === "owner"
                  ? "bg-[var(--accent)] text-white"
                  : "text-[var(--text-muted)]"
              }`}
            >
              Soy Dueño
            </button>
            <button
              type="button"
              onClick={() => setRole("professional")}
              className={`py-2.5 rounded-xl text-sm font-bold transition-all ${
                role === "professional"
                  ? "bg-[var(--accent)] text-white"
                  : "text-[var(--text-muted)]"
              }`}
            >
              Soy Profesional
            </button>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold mb-1 ml-1">
                Nombre
              </label>
              <input
                type="text"
                required
                value={firstname}
                onChange={(e) => setFirstname(e.target.value)}
                className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-2.5 px-4 focus:outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
              />
            </div>
            <div>
              <label className="block text-xs font-bold mb-1 ml-1">
                Apellido
              </label>
              <input
                type="text"
                required
                value={lastname}
                onChange={(e) => setLastname(e.target.value)}
                className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-2.5 px-4 focus:outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold mb-1 ml-1">
              Correo (Username)
            </label>
            <div className="relative">
              <Mail className="absolute left-4 top-3 w-4 h-4 text-[var(--accent)]" />
              <input
                type="email"
                required
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="ej: test@gg.com"
                className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-2.5 pl-11 pr-4 focus:outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold mb-1 ml-1">
              Contraseña
            </label>
            <div className="relative">
              <Lock className="absolute left-4 top-3 w-4 h-4 text-[var(--accent)]" />
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full bg-[var(--bg-main)] border border-[var(--border-color)] rounded-xl py-2.5 pl-11 pr-4 focus:outline-none focus:ring-2 focus:ring-[var(--color-terracotta)]"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-[var(--color-terracotta)] hover:bg-[#c4664a] text-white font-bold py-3.5 rounded-xl shadow-lg transition-all mt-4 disabled:opacity-50"
          >
            {loading ? "Procesando..." : "Registrarse"}
          </button>
        </form>

        <p className="text-center text-[var(--text-muted)] text-sm mt-6">
          ¿Ya tienes cuenta?{" "}
          <button
            onClick={onSwitchToLogin}
            className="text-[var(--color-terracotta)] font-bold hover:underline"
          >
            Inicia sesión
          </button>
        </p>
      </div>
    </div>
  );
};

export default RegisterPage;
