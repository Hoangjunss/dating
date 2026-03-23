// ============================================================
//  UserCard.jsx
// ============================================================
import { useState } from "react";
import { Heart, X, MessageCircle, MapPin, Users } from "lucide-react";

const UserCard = ({ user, onLike, onSkip, onMessage, isOnline }) => {
  const [imgLoaded,    setImgLoaded]    = useState(false);
  const [localLiked,   setLocalLiked]   = useState(false);
  const [localSkipped, setLocalSkipped] = useState(false);
  const [hovered,      setHovered]      = useState(false);

  const done = localLiked || localSkipped;

  const handleLike = async (e) => {
    e.stopPropagation();
    if (done) return;
    setLocalLiked(true);
    await onLike(user);
  };
  const handleSkip = async (e) => {
    e.stopPropagation();
    if (done) return;
    setLocalSkipped(true);
    await onSkip(user);
  };
  const handleMessage = (e) => { e.stopPropagation(); onMessage(user); };

  const interests = user.interests || ["Chat", "Bạn bè", "Kết nối"];
  const age = user.age || "";

  return (
    <div
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        aspectRatio: "3/4",
        borderRadius: "20px",
        overflow: "hidden",      /* ← clip mọi thứ bên trong */
        position: "relative",
        cursor: "pointer",
        userSelect: "none",
        background: "#111",
        boxShadow: hovered && !done
          ? "0 24px 56px rgba(0,0,0,0.55), 0 0 0 2px rgba(244,63,94,0.6)"
          : "0 4px 16px rgba(0,0,0,0.2)",
        transform: hovered && !done ? "translateY(-6px) scale(1.015)" : "translateY(0) scale(1)",
        transition: "transform 0.4s cubic-bezier(0.34,1.56,0.64,1), box-shadow 0.35s ease",
      }}
    >
      {/* ── Ảnh ── */}
      {!imgLoaded && (
        <div style={{ position:"absolute", inset:0, display:"flex", alignItems:"center", justifyContent:"center", background:"#222" }}>
          <Users style={{ width:44, height:44, opacity:0.15 }} />
        </div>
      )}
      <img
        src={user.profilePic || "/avatar.png"}
        alt={user.fullName}
        draggable={false}
        style={{
          position: "absolute", inset: 0,
          width: "100%", height: "100%",
          objectFit: "cover",
          opacity: imgLoaded ? 1 : 0,
          transform: hovered && !done ? "scale(1.12)" : "scale(1.0)",
          transition: "transform 0.7s cubic-bezier(0.25,0.46,0.45,0.94), opacity 0.3s",
          transformOrigin: "center 30%",
        }}
        onLoad={() => setImgLoaded(true)}
      />

      {/* ── Gradient tối dần khi hover ── */}
      <div style={{
        position: "absolute", inset: 0, pointerEvents: "none",
        background: hovered && !done
          ? "linear-gradient(to top, rgba(0,0,0,0.95) 42%, rgba(0,0,0,0.3) 70%, transparent 100%)"
          : "linear-gradient(to top, rgba(0,0,0,0.65) 22%, transparent 55%)",
        transition: "background 0.4s ease",
      }} />

      {/* ── Nút tim (luôn hiện) ── */}
      <button
        onClick={handleLike}
        disabled={done}
        style={{
          position: "absolute", top: 10, right: 10, zIndex: 30,
          width: 38, height: 38, borderRadius: "50%",
          border: "none", cursor: done ? "default" : "pointer",
          display: "flex", alignItems: "center", justifyContent: "center",
          background: localLiked ? "linear-gradient(135deg,#f43f5e,#e11d48)" : "rgba(0,0,0,0.45)",
          backdropFilter: "blur(10px)",
          boxShadow: localLiked ? "0 4px 18px rgba(244,63,94,0.65)" : "0 2px 8px rgba(0,0,0,0.4)",
          transform: hovered ? "scale(1.15)" : "scale(1)",
          transition: "transform 0.3s cubic-bezier(0.34,1.56,0.64,1), background 0.2s, box-shadow 0.3s",
          opacity: done && !localLiked ? 0.35 : 1,
        }}
      >
        <Heart style={{ width:17, height:17, color:"#fff", fill: localLiked ? "#fff" : "none", strokeWidth: localLiked ? 0 : 2 }} />
      </button>

      {/* ── Nút chat (chỉ hiện khi hover) ── */}
      <button
        onClick={handleMessage}
        style={{
          position: "absolute", top: 10, left: 10, zIndex: 30,
          width: 38, height: 38, borderRadius: "50%",
          border: "none", cursor: "pointer",
          display: "flex", alignItems: "center", justifyContent: "center",
          background: "rgba(0,0,0,0.45)", backdropFilter: "blur(10px)",
          boxShadow: "0 2px 8px rgba(0,0,0,0.4)",
          opacity: hovered ? 1 : 0,
          transform: hovered ? "scale(1) translateX(0)" : "scale(0.5) translateX(-10px)",
          transition: "opacity 0.25s ease, transform 0.35s cubic-bezier(0.34,1.56,0.64,1)",
          pointerEvents: hovered ? "auto" : "none",
        }}
      >
        <MessageCircle style={{ width:17, height:17, color:"#fff" }} />
      </button>

      {/* ── Badge online ── */}
      {isOnline && (
        <div style={{
          position: "absolute", top: 10, left: "50%", transform: "translateX(-50%)",
          zIndex: 30, display: "flex", alignItems: "center", gap: 5,
          background: "rgba(16,185,129,0.9)", backdropFilter: "blur(6px)",
          color: "#fff", fontSize: 11, fontWeight: 700,
          padding: "3px 10px", borderRadius: 99,
          boxShadow: "0 2px 10px rgba(16,185,129,0.5)",
          whiteSpace: "nowrap",
        }}>
          <span style={{ width:6, height:6, background:"#fff", borderRadius:"50%", animation:"onlinePulse 1.5s infinite" }} />
          Online
        </div>
      )}

      {/* ── Overlay thích ── */}
      {localLiked && (
        <div style={{ position:"absolute", inset:0, zIndex:20, pointerEvents:"none", display:"flex", alignItems:"center", justifyContent:"center", background:"rgba(244,63,94,0.2)", backdropFilter:"blur(2px)" }}>
          <div style={{ background:"rgba(255,255,255,0.15)", borderRadius:"50%", padding:20, animation:"cardPop 0.4s cubic-bezier(0.34,1.56,0.64,1)" }}>
            <Heart style={{ width:54, height:54, color:"#fff", fill:"#fff", filter:"drop-shadow(0 4px 14px rgba(244,63,94,0.9))" }} />
          </div>
        </div>
      )}

      {/* ── Overlay bỏ qua ── */}
      {localSkipped && (
        <div style={{ position:"absolute", inset:0, zIndex:20, pointerEvents:"none", display:"flex", alignItems:"center", justifyContent:"center", background:"rgba(0,0,0,0.4)", backdropFilter:"blur(3px)" }}>
          <div style={{ background:"rgba(255,255,255,0.1)", borderRadius:"50%", padding:20 }}>
            <X style={{ width:54, height:54, color:"rgba(255,255,255,0.8)" }} />
          </div>
        </div>
      )}

      {/* ══════════════════════════════════════════════
          HOVER PANEL — position absolute, trượt lên
          KHÔNG dùng translateY trên wrapper nữa
          Mọi thứ nằm gọn trong overflow:hidden
      ══════════════════════════════════════════════ */}

      {/* Panel thông tin hover — slide từ bottom lên */}
      <div style={{
        position: "absolute",
        left: 0, right: 0, bottom: 0,
        zIndex: 25,
        padding: "0 14px 14px",
        transform: hovered && !done ? "translateY(0)" : "translateY(100%)",
        transition: "transform 0.4s cubic-bezier(0.4,0,0.2,1)",
      }}>
        {/* Location */}
        {user.location && (
          <div style={{ display:"flex", alignItems:"center", gap:4, marginBottom:7 }}>
            <MapPin style={{ width:11, height:11, color:"#fb7185", flexShrink:0 }} />
            <span style={{ color:"rgba(255,255,255,0.8)", fontSize:12 }}>{user.location}</span>
          </div>
        )}

        {/* Tags */}
        <div style={{ display:"flex", flexWrap:"wrap", gap:5, marginBottom:10 }}>
          {interests.slice(0, 4).map((tag) => (
            <span key={tag} style={{
              fontSize:10, fontWeight:700, color:"#fff",
              letterSpacing:"0.06em", textTransform:"uppercase",
              padding:"3px 10px", borderRadius:99,
              border:"1.5px solid rgba(255,255,255,0.35)",
              background:"rgba(255,255,255,0.1)",
              backdropFilter:"blur(8px)",
            }}>
              {tag}
            </span>
          ))}
        </div>

        {/* Tên (trong panel) */}
        <div style={{ display:"flex", alignItems:"center", gap:7, marginBottom:10 }}>
          <p style={{ color:"#fff", fontWeight:800, fontSize:16, lineHeight:1.2, textShadow:"0 2px 8px rgba(0,0,0,0.6)", margin:0 }}>
            {user.fullName}{age ? `, ${age}` : ""}
          </p>
          {isOnline && <div style={{ width:8, height:8, background:"#10b981", borderRadius:"50%", border:"2px solid #fff", flexShrink:0 }} />}
        </div>

        {/* Buttons */}
        <div style={{ display:"flex", gap:8 }}>
          <button
            onClick={handleLike}
            disabled={done}
            onMouseEnter={e => { if(!done){ e.currentTarget.style.transform="scale(1.04)"; e.currentTarget.style.boxShadow="0 10px 30px rgba(244,63,94,0.7)"; }}}
            onMouseLeave={e => { e.currentTarget.style.transform="scale(1)"; e.currentTarget.style.boxShadow="0 5px 20px rgba(244,63,94,0.5)"; }}
            style={{
              flex:1, display:"flex", alignItems:"center", justifyContent:"center", gap:7,
              padding:"10px 0", borderRadius:14, border:"none",
              cursor: done ? "default" : "pointer",
              background: done ? "rgba(244,63,94,0.4)" : "linear-gradient(135deg,#f43f5e,#dc2626)",
              color:"#fff", fontWeight:800, fontSize:13, letterSpacing:"0.05em",
              boxShadow: done ? "none" : "0 5px 20px rgba(244,63,94,0.5)",
              transition: "transform 0.18s, box-shadow 0.18s",
              opacity: done ? 0.5 : 1,
            }}
          >
            <Heart style={{ width:15, height:15, fill:"#fff", color:"#fff" }} />
            THÍCH NGAY
          </button>

          <button
            onClick={handleSkip}
            disabled={done}
            onMouseEnter={e => { if(!done) e.currentTarget.style.background="rgba(255,255,255,0.25)"; }}
            onMouseLeave={e => { e.currentTarget.style.background="rgba(255,255,255,0.12)"; }}
            style={{
              width:42, height:42, borderRadius:12,
              border:"1.5px solid rgba(255,255,255,0.28)",
              background:"rgba(255,255,255,0.12)", backdropFilter:"blur(8px)",
              display:"flex", alignItems:"center", justifyContent:"center",
              cursor: done ? "default" : "pointer", color:"#fff",
              transition:"background 0.2s",
              opacity: done ? 0.3 : 1,
            }}
          >
            <X style={{ width:19, height:19 }} />
          </button>
        </div>
      </div>

      {/* Tên + location khi KHÔNG hover (luôn visible ở bottom) */}
      <div style={{
        position: "absolute", left: 0, right: 0, bottom: 0, zIndex: 24,
        padding: "0 14px 14px",
        opacity: hovered && !done ? 0 : 1,
        transition: "opacity 0.25s ease",
        pointerEvents: "none",
      }}>
        <p style={{ color:"#fff", fontWeight:800, fontSize:16, lineHeight:1.2, textShadow:"0 2px 8px rgba(0,0,0,0.7)", margin:0 }}>
          {user.fullName}{age ? `, ${age}` : ""}
        </p>
        {user.location && (
          <div style={{ display:"flex", alignItems:"center", gap:4, marginTop:3 }}>
            <MapPin style={{ width:11, height:11, color:"#fb7185", flexShrink:0 }} />
            <span style={{ color:"rgba(255,255,255,0.6)", fontSize:12 }}>{user.location}</span>
          </div>
        )}
      </div>

      <style>{`
        @keyframes cardPop {
          0%   { transform: scale(0.3); opacity: 0; }
          60%  { transform: scale(1.3); }
          100% { transform: scale(1);   opacity: 1; }
        }
        @keyframes onlinePulse {
          0%,100% { opacity: 1; }
          50%     { opacity: 0.4; }
        }
      `}</style>
    </div>
  );
};

export default UserCard;