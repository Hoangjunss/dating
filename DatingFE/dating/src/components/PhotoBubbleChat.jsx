const PhotoBubble = ({ message }) => {
  const isUploading = message.status === "uploading";
  const isFailed    = message.status === "failed";

  return (
    <div className="relative inline-block">
      <img
        src={message.photo}
        alt="Attachment"
        className={`max-w-[250px] md:max-w-[300px] rounded-xl border-4 border-white shadow-inner transition-opacity duration-300 ${
          isUploading ? "opacity-50" : "opacity-100"
        }`}
      />
      {isUploading && (
        <div className="absolute inset-0 flex items-center justify-center">
          <span className="size-8 border-2 border-white border-t-transparent rounded-full animate-spin block" />
        </div>
      )}
      {isFailed && (
        <div className="absolute inset-0 flex items-center justify-center bg-black/40 rounded-xl">
          <div className="flex flex-col items-center gap-1 text-white">
            <AlertCircle size={20} />
            <span className="text-[10px] font-medium">Gửi thất bại</span>
          </div>
        </div>
      )}
    </div>
  );
};

export default PhotoBubble;