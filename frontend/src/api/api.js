import axios from 'axios';

// Creamos una instancia personalizada
const api = axios.create({
    // Aquí usamos la variable que creamos en el .env
    baseURL: import.meta.env.VITE_API_BASE_URL,
});

// INTERCEPTOR DE PETICIONES: Antes de que la llamada salga al servidor
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token'); // Tu Access Token
        if (token) {
            // Le ponemos la "llave" a la petición
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// INTERCEPTOR DE RESPUESTAS: Cuando el servidor nos contesta o nos da error
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        // Guardamos la petición original que falló
        const originalRequest = error.config;

        // Si el servidor dice 401 (Token vencido) y NO hemos reintentado ya esta petición
        if (error.response && error.response.status === 401 && !originalRequest._retry) {

            // Si el error 401 viene del login o del mismo refresh, no hacemos nada para evitar un bucle infinito
            if (originalRequest.url.includes('/auth/login') || originalRequest.url.includes('/auth/refresh')) {
                return Promise.reject(error);
            }

            originalRequest._retry = true; // Marcamos que ya estamos intentando arreglar esto
            const refreshToken = localStorage.getItem('refreshToken');

            if (refreshToken) {
                try {
                    // 1. Pedimos un nuevo token.
                    // OJO: Usamos axios normal, NO nuestra instancia 'api', para no disparar este interceptor otra vez.
                    const response = await axios.post(`${import.meta.env.VITE_API_BASE_URL}/api/auth/refresh`, {
                        refreshToken: refreshToken
                    });

                    // 2. Extraemos los nuevos tokens que nos dio tu backend
                    const { accessToken, refreshToken: newRefreshToken } = response.data;

                    // 3. Los guardamos en el LocalStorage
                    localStorage.setItem('token', accessToken);
                    localStorage.setItem('refreshToken', newRefreshToken);

                    // 4. Actualizamos el token en la petición original que había fallado
                    originalRequest.headers['Authorization'] = `Bearer ${accessToken}`;

                    // 5. Repetimos la petición original. ¡El usuario no notará nada!
                    return api(originalRequest);
                } catch (refreshError) {
                    // Si el Refresh Token también expiró o es inválido, cerramos sesión de verdad
                    console.log("El Refresh Token expiró. Limpiando sesión...");
                    localStorage.clear(); // Limpia todo (tokens, user, rol)
                    window.location.href = '/login';
                    return Promise.reject(refreshError);
                }
            } else {
                // Si dio 401 y no hay Refresh Token guardado, pa' fuera
                localStorage.clear();
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
);

export default api;