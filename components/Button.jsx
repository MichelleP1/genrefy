export const Button = ({ onClick, title }) => {
  return (
    <button className="btn-spotify button" onClick={onClick}>
      {title}
    </button>
  );
};
