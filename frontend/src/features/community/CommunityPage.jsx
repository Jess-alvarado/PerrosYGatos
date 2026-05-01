import { MessageSquare, AlertCircle, Clock } from 'lucide-react';

const CommunityPage = () => {
  const posts = [
    { id: 1, user: "Carlos R.", title: "Mi gato no quiere usar el arenero", tags: ["Comportamiento", "Gatos"], time: "Hace 2h" },
    { id: 2, user: "Elena M.", title: "Ladridos excesivos cuando timbre suena", tags: ["Ansiedad", "Perros"], time: "Hace 5h" }
  ];

  return (
    <div className="max-w-5xl mx-auto p-8 animate-in fade-in duration-500">
      <h3 className="text-3xl font-black text-[var(--text-main)] mb-6">
        Muro de <span className="text-[var(--color-terracotta)]">Convivencia</span>
      </h3>
      
      <div className="grid gap-6">
        {posts.map(post => (
          <div key={post.id} className="bg-[var(--bg-surface)] p-6 rounded-card border border-[var(--border-color)] shadow-sm hover:shadow-md transition-all">
            <div className="flex justify-between items-start mb-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-[var(--accent)] rounded-full flex items-center justify-center text-white font-bold">
                  {post.user[0]}
                </div>
                <div>
                  <p className="font-bold text-[var(--text-main)]">{post.user}</p>
                  <div className="flex items-center gap-1 text-xs text-[var(--text-muted)]">
                    <Clock size={12} /> {post.time}
                  </div>
                </div>
              </div>
              <AlertCircle className="text-[var(--color-terracotta)] opacity-50" />
            </div>
            
            <h4 className="text-xl font-bold text-[var(--text-main)] mb-3">{post.title}</h4>
            
            <div className="flex gap-2 mb-4">
              {post.tags.map(tag => (
                <span key={tag} className="text-xs px-3 py-1 bg-[var(--bg-main)] text-[var(--text-muted)] rounded-full border border-[var(--border-color)]">
                  {tag}
                </span>
              ))}
            </div>

            <button className="flex items-center gap-2 text-sm font-bold text-[var(--accent)] hover:underline">
              <MessageSquare size={16} /> Ofrecer asesoría profesional
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default CommunityPage;