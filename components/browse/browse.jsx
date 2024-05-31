import React, { useState, useEffect } from "react";
import { genres } from "../../lib/static/genres";
import Autocomplete from "@mui/material/Autocomplete";
import { TextField } from "@mui/material";

export const Browse = ({ onChangeGenre }) => {
  const [value, setValue] = useState(genres[0]);

  const setGenreValue = (_, newValue) => {
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
      sx={{ width: 350 }}
      renderInput={(params) => <TextField {...params} label="Genres" />}
      inputProps={{
        style: {
          padding: 5,
          color: "#fff",
        },
      }}
    />
  );
};
