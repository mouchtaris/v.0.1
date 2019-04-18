[ "${SESS:-x}" = 'x' ] && {
  printf 'SESS missing\n' 1>&2
  exit 1
}
sess="$SESS"

while true
do
  sleepdur="$(fish -c 'random 5 15')" &&
  echo sleeping $sleepdur | tee -a $sess &&
  sleep $sleepdur &&
  ruby v.rb | tee -a $sess | tee /dev/stderr | say &&
  true || exit 1
done
