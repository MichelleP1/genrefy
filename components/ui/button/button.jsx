import { btn_spotify } from "./button.module.scss";

export const Button = ({ title, onClick, className = btn_spotify }) => {
  return (
    <button className={className} onClick={onClick}>
      {title}
    </button>
  );
};
