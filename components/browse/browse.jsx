import React, { useState, useEffect } from "react";
import styles from "./browse.module.scss";
import { genres } from "../../lib/static/genres";
import Autocomplete from "@mui/material/Autocomplete";
import { TextField } from "@mui/material";

export const Browse = ({ onChangeGenre }) => {
  //onChangeGenre(null, genre)}
  const [value, setValue] = useState(genres[0]);

  const setGenreValue = (event, newValue) => {
    setValue(newValue);
    onChangeGenre(null, newValue);
  };

  return (
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
  );
};
