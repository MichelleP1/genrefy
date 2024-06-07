import React, { useState } from "react";
import { genres } from "../../lib/static/genres";
import Autocomplete from "@mui/material/Autocomplete";
import { TextField } from "@mui/material";
import { FaArrowRightToBracket } from "react-icons/fa6";
import styles from "./browse.module.scss";
import { Button } from "../ui/button/button";
import { btn_menu } from "../ui/button/button.module.scss";

export const Browse = ({ onChangeGenre }) => {
  const [value, setValue] = useState(genres[0]);

  const setGenreValue = (_, newValue) => {
    setValue(newValue);
    onChangeGenre(null, newValue);
  };

  const logout = () => {
    console.log("logout");
  };

  return (
    <div className={styles.container}>
      <div className="autocomplete">
        <Autocomplete
          id="combo-box-demo"
          value={value}
          onChange={(event, newValue) => {
            setGenreValue(event, newValue);
          }}
          disableClearable
          options={genres}
          sx={{ width: 300 }}
          renderInput={(params) => <TextField {...params} label="Genres" />}
        />
      </div>
      <div className={styles.logout}>
        <Button
          title={<FaArrowRightToBracket />}
          onClick={logout}
          className={btn_menu}
        />
      </div>
    </div>
  );
};
